package be.xvrt.gradle.plugin.test

import be.xvrt.gradle.plugin.release.scm.ScmTestUtil
import com.google.common.collect.Lists
import org.eclipse.jgit.lib.Repository
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.fail

abstract class IntegrationTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private File remoteDir
    private File localDir

    private File buildFile
    private File propertiesFile

    protected Repository remoteRepository
    protected Repository localRepository

    @Before
    void setUp() {
        remoteDir = temporaryFolder.newFolder()
        localDir = temporaryFolder.newFolder()

        buildFile = writeBuildFile remoteDir
        propertiesFile = writePropertiesFile remoteDir
    }

    private static File writeBuildFile( File projectDir ) {
        def pluginPath = findPluginPath()

        // TODO: Find reliable way to automatically add plugin dependencies to build script.
        def buildFile = new File( projectDir, 'build.gradle' )
        buildFile.withWriter { w ->
            w.writeLine 'buildscript {'
            w.writeLine '  repositories {'
            w.writeLine '    mavenCentral()'
            w.writeLine '  }'
            w.writeLine '  dependencies {'
            w.writeLine "    classpath files( '${pluginPath}' )"
            w.writeLine '    classpath "org.eclipse.jgit:org.eclipse.jgit:3.5.1.201410131835-r"'
            w.writeLine '    classpath "org.apache.commons:commons-io:1.3.2"'
            w.writeLine '  }'
            w.writeLine '}'
            w.writeLine 'apply plugin: "be.xvrt.release"'
            w.writeLine 'dependencies {'
            w.writeLine '}'
        }

        buildFile
    }

    private static String findPluginPath() {
        def workingDir = System.getProperty 'user.dir'

        def libsDir = new File( workingDir, 'build/libs/' )
        def jarFiles = libsDir.listFiles()
        Arrays.sort( jarFiles )

        def highestBuild = jarFiles[ jarFiles.length - 1 ]
        highestBuild.absolutePath
    }

    private static File writePropertiesFile( File projectDir ) {
        def propertiesFile = new File( projectDir, 'gradle.properties' )
        propertiesFile.createNewFile()

        propertiesFile
    }

    protected void appendLineToBuildFile( String line ) {
        buildFile << line << "\n"
    }

    protected void addProperty( String key, Object value ) {
        def properties = new Properties()

        propertiesFile.withInputStream { properties.load( it ) }

        properties.put key, value
        properties.store propertiesFile.newWriter(), null
    }

    protected void cloneGitRepository() {
        remoteRepository = ScmTestUtil.createGitRepository remoteDir
        localRepository = ScmTestUtil.cloneGitRepository( localDir, remoteRepository.directory )
    }

    protected Properties getGradleProperties() {
        def properties = new Properties()

        def propertiesFile = new File( localDir, 'gradle.properties' )
        propertiesFile.withInputStream { properties.load( it ) }

        properties
    }

    protected void execute( String task, boolean shouldFail = false ) {
        def workingDir = System.getProperty 'user.dir'
        def gradleWrapper = new File( workingDir, '../gradlew' )

        def command = gradleWrapper.toString() + ' --info --stacktrace ' + task
        def process = command.execute Lists.newArrayList(), localDir
        process.waitFor()

        def expectedExitValue = shouldFail ? 1 : 0
        def actualExitValue = process.exitValue()

        if ( expectedExitValue != actualExitValue ) {
            System.out.println process.in.text
            System.err.println process.err.text
            fail()
        }
    }

}
