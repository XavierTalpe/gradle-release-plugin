package be.xvrt.gradle.plugin.release.scm

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class GitHelperTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private File projectDir
    private Repository repository
    private GitHelper gitHelper

    @Before
    void setUp() {
        projectDir = temporaryFolder.newFolder()

        repository = ScmTestUtil.createGitRepository projectDir
        gitHelper = ( GitHelper ) ScmHelperFactory.create( projectDir )
    }

    @Test
    void 'can commit'() {
        when:
        gitHelper.commit 'commitMessage'

        then:
        def commitLog = new Git( repository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( 'commitMessage', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'commit can be deleted'() {
        when:
        def commitId = gitHelper.commit 'commitMessage'
        gitHelper.deleteCommit commitId

        then:
        def commitLog = new Git( repository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'can tag'() {
        setup:
        gitHelper.commit 'commitMessage'

        when:
        gitHelper.tag '1.0.0', 'Tagging a release'

        then:
        def allTags = new Git( repository ).tagList().call()

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
        def allTags = new Git( repository ).tagList().call()

        assertEquals( 0, allTags.size() )
    }

    @Test
    void 'pushing to origin should succeed'() {
        setup:
        ScmTestUtil.createOrigin repository, temporaryFolder.newFolder()
        gitHelper.commit 'commitMessage'

        when:
        gitHelper.push 'origin'
    }

    @Test
    void 'pushing to origin should succeed with credentials'() {
        setup:
        ScmTestUtil.createOrigin repository, temporaryFolder.newFolder()

        gitHelper = ( GitHelper ) ScmHelperFactory.create( projectDir, 'user', 'pass' )
        gitHelper.commit 'commitMessage'

        when:
        gitHelper.push 'origin'
    }

    @Test( expected = ScmException.class )
    void 'pushing to origin should fail because no remote added'() {
        setup:
        gitHelper.commit 'commitMessage'

        when:
        gitHelper.push 'origin'
    }

}
