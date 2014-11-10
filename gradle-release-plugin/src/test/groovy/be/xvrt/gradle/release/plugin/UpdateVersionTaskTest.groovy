package be.xvrt.gradle.release.plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class UpdateVersionTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Project project
    private Task prepareReleaseTask
    private Task updateVersionTask

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin

        prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        updateVersionTask = project.tasks.getByName ReleasePlugin.UPDATE_VERSION_TASK
    }

    @Test
    void testNextReleaseWithSnapshotVersion() {
        setup:
        project.version = '1.0.0-SNAPSHOT'
        prepareReleaseTask.configure()

        when:
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.1-SNAPSHOT', updateVersionTask.nextVersion )
        assertEquals( '1.0.1-SNAPSHOT', project.version )
    }

    @Test
    void testNextReleaseWithNonSnapshotVersion() {
        setup:
        project.version = '1.0.0'
        prepareReleaseTask.configure()

        when:
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.1', updateVersionTask.nextVersion )
        assertEquals( '1.0.1', project.version )
    }

    @Test
    void testNextReleaseWithEmptyPropertiesFile() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )

        def project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0-SNAPSHOT'

        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        def updateVersionTask = project.tasks.getByName ReleasePlugin.UPDATE_VERSION_TASK

        when:
        prepareReleaseTask.configure()
        updateVersionTask.execute()

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertTrue( properties.isEmpty() )
    }

    @Test
    void testNextReleaseWithPropertiesFile() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile << 'version=1.0.0-SNAPSHOT'

        def project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0-SNAPSHOT' // TODO: Trigger project to read properties file instead.

        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        def updateVersionTask = project.tasks.getByName ReleasePlugin.UPDATE_VERSION_TASK

        when:
        prepareReleaseTask.configure()
        updateVersionTask.execute()

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.1-SNAPSHOT', properties.version )
    }

    @Test
    void testCustomNextVersionClosure() {
        setup:
        project.version = '1.0.0-SNAPSHOT'
        project.release {
            nextVersion = { version, wasSnapshotVersion ->
                if ( wasSnapshotVersion ) {
                    version = version + '-SNAPSHOT'
                }

                version + '-2'
            }
        }

        when:
        prepareReleaseTask.configure()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.0-SNAPSHOT-2', updateVersionTask.nextVersion )
        assertEquals( '1.0.0-SNAPSHOT-2', project.version )
    }

    @Test
    void testSetNextVersionProperty() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile << 'version=1.0.0-SNAPSHOT'

        def buildFile = temporaryFolder.newFile( 'build.gradle' )
        buildFile.withWriter { w ->
            w.writeLine 'buildscript {'
            w.writeLine '  dependencies {'
            w.writeLine '    classpath files( "/home/xaviert/projects/gradle-release-plugin/gradle-release-plugin/build/libs/release-plugin-0.3.0-SNAPSHOT.jar" )'
            w.writeLine '  }'
            w.writeLine '}'
            w.writeLine 'apply plugin: "be.xvrt.release"'
        }

        when:
        def command = 'gradle release -PnextVersion=2.0.0-SNAPSHOT'
        def process = command.execute( null, temporaryFolder.root )
        process.waitFor()

        then:
        println "return code: ${process.exitValue()}"
        println "stderr: ${process.err.text}"
        println "stdout: ${process.in.text}"

        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '2.0.0-SNAPSHOT', properties.version )
    }

}
