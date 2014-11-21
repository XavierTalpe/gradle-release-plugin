package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.exception.InvalidDependencyException
import be.xvrt.gradle.plugin.release.scm.ScmException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskExecutionException
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
        project.apply plugin: 'groovy'
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0-SNAPSHOT'

        prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
    }

    @Test
    void 'snapshot version becomes release version'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()

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
        prepareReleaseTask.execute()

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
        prepareReleaseTask.execute()

        then:
        assertEquals( '1.0.0', prepareReleaseTask.originalVersion )
        assertEquals( '1.0.0-RC1', prepareReleaseTask.releaseVersion )
        assertFalse( prepareReleaseTask.wasSnapshotVersion() )
    }

    @Test
    void 'non-snapshot dependencies don\'t trigger exception'() {
        setup:
        project.configurations { myConfig }
        project.dependencies {
            compile 'group:name:1.0.0'
            compile 'group:name:'
            myConfig 'group::5.1.2.3'
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()

        then:
        assertTrue( true )
    }

    @Test
    void 'snapshot dependencies should trigger exception'() {
        setup:
        project.configurations { myConfig }
        project.dependencies {
            compile 'group:name:1.0.0-SNAPSHOT'
            compile 'group:name:'
            myConfig 'group::5.1.2.3-SNAPSHOT'
        }

        when:
        def cause = null

        try {
            prepareReleaseTask.configure()
            prepareReleaseTask.execute()
            fail()
        }
        catch ( Exception exception ) {
            assertTrue( exception instanceof TaskExecutionException )
            cause = exception.cause
        }

        then:
        assertNotNull( cause )
        assertTrue( cause instanceof InvalidDependencyException )
        assertEquals( 'Cannot release project with SNAPSHOT dependencies:\n' +
                      'test - group:name:1.0.0-SNAPSHOT\n' +
                      'test - group::5.1.2.3-SNAPSHOT\n', cause.message )
    }

    @Test
    void 'snapshot dependencies are not checked'() {
        setup:
        project.dependencies {
            compile 'group:name:1.0.0-SNAPSHOT'
        }

        project.release {
            checkDependencies = false
        }

        when:
        try {
            prepareReleaseTask.configure()
            prepareReleaseTask.execute()

            then:
            assertTrue( true )
        }
        catch ( Exception ignored ) {
            fail()
        }
    }

    @Test
    void 'project version is rolled back'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()

        then:
        assertEquals( '1.0.0', project.version )

        when:
        prepareReleaseTask.rollback( new ScmException( "Test" ) )

        then:
        assertEquals( '1.0.0-SNAPSHOT', project.version )
    }

}
