package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

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

}
