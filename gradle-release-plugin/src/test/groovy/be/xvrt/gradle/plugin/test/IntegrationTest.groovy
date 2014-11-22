package be.xvrt.gradle.plugin.test

import be.xvrt.gradle.plugin.release.scm.ScmTestUtil
import org.eclipse.jgit.lib.Repository
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.fail

abstract class IntegrationTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private File projectDir
    private File buildFile
    private File propertiesFile

    @Before
    void setUp() {
        projectDir = temporaryFolder.newFolder()

        buildFile = writeBuildFile projectDir
        propertiesFile = writePropertiesFile projectDir
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

    protected Properties getProperties() {
        def properties = new Properties()

        propertiesFile.withInputStream { properties.load( it ) }

        properties
    }

    protected void addProperty( String key, Object value ) {
        def properties = new Properties()

        propertiesFile.withInputStream { properties.load( it ) }

        properties.put key, value
        properties.store propertiesFile.newWriter(), null
    }

    protected void execute( String task, boolean shouldFail = false ) {
        def workingDir = System.getProperty 'user.dir'
        def gradleWrapper = new File( workingDir, '../gradlew' )

        def command = gradleWrapper.toString() + ' --info --stacktrace ' + task
        def process = command.execute null, projectDir
        process.waitFor()

        def expectedExitValue = shouldFail ? 1 : 0
        def actualExitValue = process.exitValue()

        if ( expectedExitValue != actualExitValue ) {
            System.out.println process.in.text
            System.err.println process.err.text
            fail()
        }
    }

    protected Repository enableGit( boolean createOrigin = true ) {
        def repository = ScmTestUtil.createGitRepository projectDir

        if ( createOrigin ) {
            ScmTestUtil.createOrigin repository, temporaryFolder.newFolder()
        }

        repository
    }

}
