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

    private Repository remoteRepository
    private Repository localRepository

    private GitHelper gitHelper

    @Before
    void setUp() {
        remoteRepository = ScmTestUtil.createGitRepository temporaryFolder.newFolder()
        localRepository = ScmTestUtil.cloneGitRepository( temporaryFolder.newFolder(), remoteRepository.directory )

        gitHelper = new GitHelper( localRepository.directory )
    }

    @Test
    void 'can commit'() {
        when:
        gitHelper.commit 'commitMessage'

        then:
        def commitLog = new Git( localRepository ).log().call().toList()

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
        def commitLog = new Git( localRepository ).log().call().toList()

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
        def allTags = new Git( localRepository ).tagList().call()

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
        def allTags = new Git( localRepository ).tagList().call()

        assertEquals( 0, allTags.size() )
    }

    @Test
    void 'pushing to origin should succeed'() {
        when:
        gitHelper.commit 'commitMessage'
        gitHelper.tag '1.0.0', 'Tagging a release'
        gitHelper.push 'origin'

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'commitMessage', commitLog.get( 0 ).shortMessage )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )

        def allTags = new Git( remoteRepository ).tagList().call()

        assertEquals( 1, allTags.size() )
        assertEquals( 'refs/tags/1.0.0', allTags.get( 0 ).getName() )
    }

    @Test
    void 'pushing to origin should succeed with credentials'() {
        setup:
        gitHelper = new GitHelper( localRepository.directory, 'user', 'pass' )

        when:
        gitHelper.commit 'commitMessage'
        gitHelper.tag '1.0.0', 'Tagging a release'
        gitHelper.push 'origin'

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'commitMessage', commitLog.get( 0 ).shortMessage )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )

        def allTags = new Git( remoteRepository ).tagList().call()

        assertEquals( 1, allTags.size() )
        assertEquals( 'refs/tags/1.0.0', allTags.get( 0 ).getName() )
    }

    @Test( expected = ScmException.class )
    void 'pushing to origin should fail because no remote added'() {
        setup:
        ScmTestUtil.removeOriginFrom localRepository

        when:
        gitHelper.commit 'commitMessage'
        gitHelper.tag '1.0.0', 'Tagging a release'
        gitHelper.push 'origin'
    }

}
