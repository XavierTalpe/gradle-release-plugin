package be.xvrt.gradle.release.plugin.scm

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class GitHelperTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Repository repository
    private GitHelper gitHelper

    @Before
    void setUp() {
        repository = ScmTestUtil.createGitRepository( temporaryFolder.root )

        gitHelper = ( GitHelper ) ScmHelperFactory.create( temporaryFolder.root )
    }

    @After
    void tearDown() throws Exception {
        repository.close()
    }

    @Test
    void testCommit() {
        when:
        gitHelper.commit( 'Empty commit' )

        then:
        Iterable<RevCommit> commitLog = new Git( repository ).log().call();

        def nbCommits = 0;
        for ( RevCommit commit : commitLog ) {
            assertEquals( 'Empty commit', commit.getShortMessage() )
            nbCommits++;
        }

        assertEquals( 1, nbCommits )
    }

    @Test
    void testTag() {
        setup:
        gitHelper.commit( 'Empty commit' )

        when:
        gitHelper.tag( '1.0.0', 'Tagging a release' )

        then:
        def allTags = new Git( repository ).tagList().call();

        assertEquals( 1, allTags.size() )
        assertEquals( 'refs/tags/1.0.0', allTags.get( 0 ).getName() )
    }

}