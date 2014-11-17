package be.xvrt.gradle.plugin.test
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

abstract class IntegrationTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private File buildFile
    private File propertiesFile

    @Before
    void setUp() {
        writeBuildFile()
        writePropertiesFile()
    }

    private void writeBuildFile() {
        def workingDir = System.getProperty 'user.dir'
        // TODO: Dynamically retrieve latest JAR file in folder instead of explicit version number.
        def pluginPath = new File( workingDir, 'build/libs/gradle-release-plugin-0.4.0-SNAPSHOT.jar' )

        buildFile = temporaryFolder.newFile 'build.gradle'
        buildFile.withWriter { w ->
            w.writeLine 'buildscript {'
            w.writeLine '  dependencies {'
            w.writeLine "    classpath files( '${pluginPath}' )"
            w.writeLine '  }'
            w.writeLine '}'
            w.writeLine 'apply plugin: "be.xvrt.release"'
        }
    }

    private void writePropertiesFile() {
        propertiesFile = temporaryFolder.newFile 'gradle.properties'
    }

    protected void appendToBuildFile( String line ) {
        buildFile << line
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

    protected void execute( String task ) {
        def workingDir = System.getProperty 'user.dir'
        def gradleWrapper = new File( workingDir, '../gradlew' )

        def command = gradleWrapper.toString() + ' ' + task
        def process = command.execute null, temporaryFolder.root
        process.waitFor()

        def exitValue = process.exitValue()
        if ( exitValue == 1 ) {
            System.out.println process.in.text
            System.err.println process.err.text
        }

        assertEquals 0, exitValue
    }

}
