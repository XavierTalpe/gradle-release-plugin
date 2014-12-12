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

import static org.junit.Assert.*

class TagReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Repository remoteRepository
    private Repository localRepository

    private Project project
    private Task tagReleaseTask

    @Before
    void setUp() {
        def projectDir = temporaryFolder.newFolder()

        remoteRepository = ScmTestUtil.createGitRepository temporaryFolder.newFolder()
        localRepository = ScmTestUtil.cloneGitRepository projectDir, remoteRepository.directory

        project = ProjectBuilder.builder().withProjectDir( projectDir ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
    }

    @Test
    void 'tag is pushed when no errors occur'() {
        when:
        tagReleaseTask.execute()

        then:
        def tagList = new Git( remoteRepository ).tagList().call()

        assertEquals( 1, tagList.size() )
        assertEquals( 'refs/tags/1.0.0', tagList.get( 0 ).getName() )
    }

    @Test
    void 'executing the task will not create a tag when SCM support is disabled'() {
        setup:
        project.release {
            scmDisabled = true
        }

        when:
        tagReleaseTask.execute()

        then:
        def tagList = new Git( localRepository ).tagList().call()
        assertEquals( 0, tagList.size() )
    }

    @Test
    public void 'override tag name and message'() throws Exception {
        project.release {
            releaseTag = '0.2.3'
            releaseTagMessage = 'Custom tag for %version.'
        }

        when:
        tagReleaseTask.execute()

        then:
        def tagList = new Git( remoteRepository ).tagList().call()

        assertEquals( 1, tagList.size() )
        assertEquals( 'refs/tags/0.2.3', tagList.get( 0 ).getName() )
    }

    @Test
    void 'tag is rolled back when push fails'() {
        setup:
        ScmTestUtil.removeOriginFrom localRepository

        when:
        try {
            tagReleaseTask.execute()
            fail()
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        def tagList = new Git( localRepository ).tagList().call()
        assertEquals( 0, tagList.size() )
    }

}
