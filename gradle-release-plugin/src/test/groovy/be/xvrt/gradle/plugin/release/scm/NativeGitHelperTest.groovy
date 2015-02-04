package be.xvrt.gradle.plugin.release.scm

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class NativeGitHelperTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Repository remoteRepository
    private Repository localRepository

    private NativeGitHelper gitHelper

    @Before
    void setUp() {
        def remoteFolder = temporaryFolder.newFolder()

        def initialFile = new File( remoteFolder, 'writable.file' )
        initialFile << 'initial data'

        remoteRepository = ScmTestUtil.createGitRepository remoteFolder
        localRepository = ScmTestUtil.cloneGitRepository temporaryFolder.newFolder(), remoteRepository.directory

        gitHelper = new NativeGitHelper( localRepository.directory )
    }

    @Test
    void 'can commit'() {
        setup:
        createLocalChange()

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
        setup:
        createLocalChange()

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
        createLocalChange()
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
        createLocalChange()
        gitHelper.commit 'commitMessage'

        when:
        def tagId = gitHelper.tag '1.0.0', 'Tagging a release'
        gitHelper.deleteTag tagId

        then:
        def allTags = new Git( localRepository ).tagList().call()

        assertEquals( 0, allTags.size() )
    }

    @Test
    void 'pushing commit to origin should succeed'() {
        setup:
        createLocalChange()

        when:
        gitHelper.commit 'commitMessage'
        gitHelper.push 'origin'

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'commitMessage', commitLog.get( 0 ).shortMessage )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
    }

    @Test
    void 'pushing tag to origin should succeed'() {
        when:
        def tag = gitHelper.tag( '1.0.0', 'Tagging a release' )
        gitHelper.pushTag 'origin', tag

        then:
        def allTags = new Git( localRepository ).tagList().call()

        assertEquals( 1, allTags.size() )
        assertEquals( 'refs/tags/1.0.0', allTags.get( 0 ).getName() )
    }

    @Test
    void 'pushing commit and tag to origin should succeed'() {
        setup:
        createLocalChange()

        when:
        gitHelper.commit 'commitMessage'
        def tag = gitHelper.tag( '1.0.0', 'Tagging a release' )
        gitHelper.push 'origin'
        gitHelper.pushTag 'origin', tag

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
    void 'pushing commit to origin should fail because no remote added'() {
        setup:
        createLocalChange()
        ScmTestUtil.removeOriginFrom localRepository

        when:
        gitHelper.commit 'commitMessage'
        gitHelper.push 'origin'
    }

    @Test( expected = ScmException.class )
    void 'pushing tag to origin should fail because no remote added'() {
        setup:
        ScmTestUtil.removeOriginFrom localRepository

        when:
        def tag = gitHelper.tag( '1.0.0', 'Tagging a release' )
        gitHelper.pushTag 'origin', tag
    }

    private void createLocalChange() {
        def fileToCommit = new File( localRepository.directory.parentFile, 'writable.file' )
        fileToCommit << 'update!'
    }

}
