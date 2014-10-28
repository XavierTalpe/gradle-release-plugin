package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmTestUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class TagReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Repository repository

    private Project project
    private Task tagReleaseTask

    @Before
    void setUp() {
        repository = ScmTestUtil.createGitRepository( temporaryFolder.root )

        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
    }

    @Ignore
    @Test
    void testConfigureValidGitRepo() {
        tagReleaseTask.configure()
    }

    @Ignore
    @Test
    void testConfigureBrokenGitRepo() {
        def gitRepo = new File( temporaryFolder.root, '.git' )
        for ( File file : gitRepo.listFiles() ) {
            file.delete()
        }

        // Does not fail as expected
        tagReleaseTask.configure()
    }

    @Test
    void testExecuteGit() {
        when:
        tagReleaseTask.configure()
        tagReleaseTask.execute()

        then:
        verifyTag()
        verifyCommit()
    }

    private void verifyTag() {
        def allTags = new Git( repository ).tagList().call();

        assertEquals( 1, allTags.size() )
        assertEquals( 'refs/tags/1.0.0', allTags.get( 0 ).getName() )
    }

    private void verifyCommit() {
        Iterable<RevCommit> commitLog = new Git( repository ).log().call();

        def nbCommits = 0;
        for ( RevCommit commit : commitLog ) {
            assertEquals( '[Gradle Release Plugin] Saving release 1.0.0', commit.getShortMessage() )
            nbCommits++;
        }

        assertEquals( 1, nbCommits )
    }

}
