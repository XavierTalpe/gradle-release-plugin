package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.scm.ScmTestUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class CommitReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Repository remoteRepository
    private Repository localRepository

    private Project project
    private Task commitReleaseTask

    @Before
    void setUp() {
        def projectDir = temporaryFolder.newFolder()

        remoteRepository = ScmTestUtil.createGitRepository temporaryFolder.newFolder()
        localRepository = ScmTestUtil.cloneGitRepository( projectDir, remoteRepository.directory )

        project = ProjectBuilder.builder().withProjectDir( projectDir ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        commitReleaseTask = project.tasks.getByName ReleasePlugin.COMMIT_RELEASE_TASK
    }

    @Test
    void 'commit is pushed when no errors occur'() {
        when:
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( '[Gradle Release] Commit for 1.0.0.', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'executing the task will not create a commit when SCM support is disabled'() {
        setup:
        project.release {
            scmDisabled = true
        }

        when:
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( localRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

    @Test
    public void 'override commit message'() throws Exception {
        setup:
        project.release {
            releaseCommitMessage = 'Custom commit for %version.'
        }

        when:
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( 'Custom commit for 1.0.0.', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'commit is rolled back when push fails'() {
        setup:
        ScmTestUtil.removeOrigin localRepository

        when:
        try {
            commitReleaseTask.execute()
            fail()
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

}
