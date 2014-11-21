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

import static junit.framework.Assert.fail
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class CommitReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

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
        def commitLog = new Git( gradleRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( '[Gradle Release] Commit for 1.0.0.', commitLog.get( 0 ).shortMessage )
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
        def commitLog = new Git( gradleRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

    @Test
    public void 'override commit message'() throws Exception {
        setup:
        ScmTestUtil.createOrigin gradleRepository, temporaryFolder.newFolder()

        project.release {
            releaseCommitMessage = 'Custom commit for %version.'
        }

        when:
        commitReleaseTask.configure()
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( gradleRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( 'Custom commit for 1.0.0.', commitLog.get( 0 ).shortMessage )
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
        def commitLog = new Git( gradleRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

}