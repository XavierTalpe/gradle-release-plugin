package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmException
import be.xvrt.gradle.release.plugin.scm.ScmTestUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.fail
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class CommitReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Repository gradleRepository

    private Project project
    private Task commitReleaseTask

    @Before
    void setUp() {
        def repoFolder = temporaryFolder.newFolder()
        gradleRepository = ScmTestUtil.createGitRepository repoFolder

        project = ProjectBuilder.builder().withProjectDir( repoFolder ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        commitReleaseTask = project.tasks.getByName ReleasePlugin.COMMIT_RELEASE_TASK
    }

    @Test
    void 'commit is pushed when no errors occur'() {
        setup:
        ScmTestUtil.createOrigin gradleRepository, temporaryFolder.newFolder()

        when:
        commitReleaseTask.configure()
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( gradleRepository ).log().call()

        def nbCommits = 0;
        for ( RevCommit commit : commitLog ) {
            if ( !commit.getShortMessage().equals( 'HEAD' ) ) {
                assertEquals( '[Gradle Release] Commit for 1.0.0.', commit.getShortMessage() )
                nbCommits++;
            }
        }

        assertEquals( 1, nbCommits )
    }


    @Test
    void 'executing the task won\'t create a commit when SCM support is disabled'() {
        setup:
        project.release {
            scmDisabled = true
        }

        when:
        commitReleaseTask.configure()
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( gradleRepository ).log().call()
        def nbNewCommits = commitLog.count { commit ->
            !commit.getShortMessage().equals( 'HEAD' )
        }

        assertEquals( 0, nbNewCommits )
    }

    @Ignore
    @Test
    public void 'override commit message'() throws Exception {
        // TODO
    }

    @Test
    void 'commit is rolled back when push fails'() {
        when:
        commitReleaseTask.configure()

        try {
            commitReleaseTask.execute()
            fail()
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        def commitLog = new Git( gradleRepository ).log().call()
        def nbNewCommits = commitLog.count { commit ->
            !commit.getShortMessage().equals( 'HEAD' )
        }

        assertEquals( 0, nbNewCommits )
    }

}
