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
        gradleRepository = ScmTestUtil.createGitRepository( temporaryFolder.root )

        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        commitReleaseTask = project.tasks.getByName ReleasePlugin.COMMIT_RELEASE_TASK
    }

    @Test
    void testExecuteGit() {
        when:
        commitReleaseTask.configure()

        try {
            commitReleaseTask.execute()
            fail() // TODO Fails due to no origin specified.
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        verifyCommit()
    }

    private void verifyCommit() {
        Iterable<RevCommit> commitLog = new Git( gradleRepository ).log().call();

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
    void 'no commit made when SCM support is disabled'() {
        setup:
        project.release {
            scmDisabled = true
        }


        when:
        commitReleaseTask.configure()
        commitReleaseTask.execute()

        then:
        Iterable<RevCommit> commitLog = new Git( gradleRepository ).log().call();

        def nbCommits = 0;
        for ( RevCommit commit : commitLog ) {
            if ( !commit.getShortMessage().equals( 'HEAD' ) ) {
                nbCommits++;
            }
        }

        assertEquals( 0, nbCommits )
    }

}
