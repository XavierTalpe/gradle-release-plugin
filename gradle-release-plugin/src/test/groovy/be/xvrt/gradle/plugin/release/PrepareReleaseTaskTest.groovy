package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.ScmException
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
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Project project
    private Task prepareReleaseTask

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin

        prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
    }

    @Test
    void 'snapshot version becomes release version'() {
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
    void 'non-snapshot version becomes release version'() {
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
    void 'custom closure for release version should be invoked'() {
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

    @Test
    void 'rollback resets project version'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'

        when:
        prepareReleaseTask.configure()

        then:
        assertEquals( '1.0.0', project.version )

        when:
        prepareReleaseTask.rollback( new ScmException( "Test" ) )

        then:
        assertEquals( '1.0.0-SNAPSHOT', project.version )
    }

}
