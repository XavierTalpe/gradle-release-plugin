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

class PrepareNextReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Project project
    private Task prepareReleaseTask
    private Task prepareNextReleaseTask

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin

        prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        prepareNextReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_NEXT_RELEASE_TASK
    }

    @Test
    void testNextReleaseWithSnapshotVersion() {
        setup:
        project.version = '1.0.0-SNAPSHOT'
        prepareReleaseTask.configure()

        when:
        prepareNextReleaseTask.execute()

        then:
        assertEquals( '1.0.0', prepareNextReleaseTask.releasedVersion )
        assertEquals( '1.0.1-SNAPSHOT', prepareNextReleaseTask.nextVersion )
        assertEquals( '1.0.1-SNAPSHOT', project.version )
    }

    @Test
    void testNextReleaseWithNonSnapshotVersion() {
        setup:
        project.version = '1.0.0'
        prepareReleaseTask.configure()

        when:
        prepareNextReleaseTask.execute()

        then:
        assertEquals( '1.0.0', prepareNextReleaseTask.releasedVersion )
        assertEquals( '1.0.1', prepareNextReleaseTask.nextVersion )
        assertEquals( '1.0.1', project.version )
    }

    @Test
    void testNextReleaseWithEmptyPropertiesFile() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )

        def project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin

        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        def prepareNextReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_NEXT_RELEASE_TASK

        project.version = '1.0.0-SNAPSHOT'
        prepareReleaseTask.configure()

        when:
        prepareNextReleaseTask.execute()

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertTrue( properties.isEmpty() )
    }

    @Test
    void testNextReleaseWithPropertiesFile() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile.withWriter { w -> w.writeLine 'version=1.0.0-SNAPSHOT' }

        def project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin

        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        def prepareNextReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_NEXT_RELEASE_TASK

        project.version = '1.0.0-SNAPSHOT' // TODO: Trigger project to read properties file instead.
        prepareReleaseTask.configure()

        when:
        prepareNextReleaseTask.execute()

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.1-SNAPSHOT', properties.version )
    }

}
