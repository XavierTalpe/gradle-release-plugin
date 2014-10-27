package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class PrepareReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Project project
    private Task prepareReleaseTask

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin

        prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
    }

    @Test
    void testConfigureSnapshotVersion() {
        setup:
        project.version = '1.0.0-SNAPSHOT'

        when:
        prepareReleaseTask.configure()

        then:

        assertEquals( '1.0.0-SNAPSHOT', prepareReleaseTask.originalVersion )
        assertEquals( '1.0.0', prepareReleaseTask.releaseVersion )
        assertTrue( prepareReleaseTask.wasSnapshotVersion() )
    }

    @Test
    void testConfigureNonSnapshotVersion() {
        setup:
        project.version = '1.0.0'

        when:
        prepareReleaseTask.configure()

        then:

        assertEquals( '1.0.0', prepareReleaseTask.originalVersion )
        assertEquals( '1.0.0', prepareReleaseTask.releaseVersion )
        assertFalse( prepareReleaseTask.wasSnapshotVersion() )
    }

    @Test
    void testConfigureWithEmptyPropertiesFile() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )

        def project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0-SNAPSHOT'

        def prepareReleaseTask = project.tasks.getByName( ReleasePlugin.PREPARE_RELEASE_TASK )

        when:
        prepareReleaseTask.configure()

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertTrue( properties.isEmpty() )
    }

    @Test
    void testConfigureWithPropertiesFile() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile.withWriter { w -> w.writeLine 'version=1.0.0-SNAPSHOT' }

        def project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0-SNAPSHOT' // TODO: Trigger project to read properties file instead.

        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK

        when:
        prepareReleaseTask.configure()

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.0', properties.version )
    }

}
