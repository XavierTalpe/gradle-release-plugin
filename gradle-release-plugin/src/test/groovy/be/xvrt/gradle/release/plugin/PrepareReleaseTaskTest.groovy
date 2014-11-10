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
    void setUp() {
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
    void testCustomReleaseVersionClosure() {
        setup:
        project.version = '1.0.0'
        project.release {
            releaseVersion = { version ->
                version + '-RC1'
            }
        }

        when:
        prepareReleaseTask.configure()

        then:
        assertEquals( '1.0.0', prepareReleaseTask.originalVersion )
        assertEquals( '1.0.0-RC1', prepareReleaseTask.releaseVersion )
        assertFalse( prepareReleaseTask.wasSnapshotVersion() )
    }

}
