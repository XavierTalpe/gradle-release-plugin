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

class TagReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Repository gradleRepository

    private Project project
    private Task tagReleaseTask

    @Before
    void setUp() {
        gradleRepository = ScmTestUtil.createGitRepository( temporaryFolder.root )

        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
    }

    @Test
    void testExecuteGit() {
        when:
        tagReleaseTask.configure()

        try {
            tagReleaseTask.execute()
            fail() // TODO Fails due to no origin specified.
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        verifyCommit()
        verifyTag()
    }

    private void verifyCommit() {
        Iterable<RevCommit> commitLog = new Git( gradleRepository ).log().call();

        def nbCommits = 0;
        for ( RevCommit commit : commitLog ) {
            assertEquals( '[Gradle Release] Commit for 1.0.0.', commit.getShortMessage() )
            nbCommits++;
        }

        assertEquals( 1, nbCommits )
    }

    private void verifyTag() {
        def allTags = new Git( gradleRepository ).tagList().call();

        assertEquals( 1, allTags.size() )
        assertEquals( 'refs/tags/1.0.0', allTags.get( 0 ).getName() )
    }

}
