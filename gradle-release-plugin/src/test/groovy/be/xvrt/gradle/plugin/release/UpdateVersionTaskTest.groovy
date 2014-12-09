package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.scm.ScmTestUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.gradle.api.Project
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class UpdateVersionTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Repository remoteRepository
    private Repository localRepository

    private Project project
    private PrepareReleaseTask prepareReleaseTask
    private UpdateVersionTask updateVersionTask

    @Before
    void setUp() {
        def projectDir = temporaryFolder.newFolder()

        remoteRepository = ScmTestUtil.createGitRepository temporaryFolder.newFolder()
        localRepository = ScmTestUtil.cloneGitRepository( projectDir, remoteRepository.directory )

        project = ProjectBuilder.builder().withProjectDir( projectDir ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        prepareReleaseTask = project.tasks.getByName( ReleasePlugin.PREPARE_RELEASE_TASK ) as PrepareReleaseTask
        updateVersionTask = project.tasks.getByName( ReleasePlugin.UPDATE_VERSION_TASK ) as UpdateVersionTask
    }

    @Test
    void 'next version is snapshot version'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'
        project.release {
            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.1-SNAPSHOT', updateVersionTask.nextVersion )
        assertEquals( '1.0.1-SNAPSHOT', project.version )
    }

    @Test
    void 'next version is non-snapshot version'() {
        setup:
        project.version = '1.0.0'
        project.release {
            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.1', updateVersionTask.nextVersion )
        assertEquals( '1.0.1', project.version )
    }

    @Test
    void 'next version is defined by custom closure'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'
        project.release {
            nextVersion = { version, wasSnapshotVersion ->
                if ( wasSnapshotVersion ) {
                    version = version + '-SNAPSHOT'
                }

                version + '-2'
            }

            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.0-SNAPSHOT-2', updateVersionTask.nextVersion )
        assertEquals( '1.0.0-SNAPSHOT-2', project.version )
    }

    @Test
    void 'commit is pushed when no errors occur'() {
        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( '[Gradle Release] Preparing for 1.0.1.', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'executing the task will not create a commit when SCM support is disabled'() {
        setup:
        project.release {
            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( localRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'override commit message'() {
        project.release {
            updateVersionCommitMessage = 'Custom prepare for %version.'
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( 'Custom prepare for 1.0.1.', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'commit is rolled back when push fails'() {
        setup:
        ScmTestUtil.removeOrigin localRepository

        when:
        try {
            prepareReleaseTask.configure()
            prepareReleaseTask.execute()
            updateVersionTask.execute()
            fail()
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        def commitLog = new Git( localRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

}
