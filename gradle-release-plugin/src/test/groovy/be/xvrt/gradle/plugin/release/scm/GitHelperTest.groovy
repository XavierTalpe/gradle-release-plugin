package be.xvrt.gradle.plugin.release.scm

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
    void 'can commit'() {
        when:
        gitHelper.commit 'commitMessage'

        then:
        def commitLog = new Git( repository ).log().call()
        def nbCommits = 0
        for ( RevCommit commit : commitLog ) {
            if ( !commit.getShortMessage().equals( 'HEAD' ) ) {
                assertEquals( 'commitMessage', commit.getShortMessage() )
                nbCommits++
            }
        }

        assertEquals( 1, nbCommits )
    }

    @Test
    void 'commit can be deleted'() {
        when:
        def commitId = gitHelper.commit 'commitMessage'
        gitHelper.deleteCommit commitId

        then:
        def commitLog = new Git( repository ).log().call()
        def nbNewCommits = commitLog.count { commit ->
            !commit.getShortMessage().equals( 'HEAD' )
        }

        assertEquals( 0, nbNewCommits )
    }

    @Test
    void 'can tag'() {
        setup:
        gitHelper.commit 'commitMessage'

        when:
        gitHelper.tag '1.0.0', 'Tagging a release'

        then:
        def allTags = new Git( repository ).tagList().call();

        assertEquals( 1, allTags.size() )
        assertEquals( 'refs/tags/1.0.0', allTags.get( 0 ).getName() )
    }

    @Test
    void 'tag can be deleted'() {
        setup:
        gitHelper.commit 'commitMessage'

        when:
        def tagId = gitHelper.tag '1.0.0', 'Tagging a release'
        gitHelper.deleteTag tagId

        then:
        def allTags = new Git( repository ).tagList().call();

        assertEquals( 0, allTags.size() )
    }

    @Test( expected = ScmException.class )
    void 'pushing without an origin should fail'() {
        setup:
        gitHelper.commit 'commitMessage'

        when:
        gitHelper.push 'origin'
    }

}
