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

class TagReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Repository gradleRepository

    private Project project
    private Task tagReleaseTask

    @Before
    void setUp() {
        gradleRepository = ScmTestUtil.createGitRepository temporaryFolder.root

        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
    }

    @Test
    void 'tag is pushed when no errors occur'() {
        setup:
        ScmTestUtil.createOrigin gradleRepository, temporaryFolder.newFolder()

        when:
        tagReleaseTask.configure()
        tagReleaseTask.execute()

        then:
        def tagList = new Git( gradleRepository ).tagList().call()

        assertEquals( 1, tagList.size() )
        assertEquals( 'refs/tags/1.0.0', tagList.get( 0 ).getName() )
    }

    @Test
    void 'executing the task won\'t create a tag when SCM support is disabled'() {
        setup:
        project.release {
            scmDisabled = true
        }

        when:
        tagReleaseTask.configure()
        tagReleaseTask.execute()

        then:
        def tagList = new Git( gradleRepository ).tagList().call()
        assertEquals( 0, tagList.size() )
    }

    @Test
    public void 'override tag name and message'() throws Exception {
        setup:
        ScmTestUtil.createOrigin gradleRepository, temporaryFolder.newFolder()

        project.release {
            releaseTag = '0.2.3'
            releaseTagMessage = 'Custom tag for %version.'
        }

        when:
        tagReleaseTask.configure()
        tagReleaseTask.execute()

        then:
        def tagList = new Git( gradleRepository ).tagList().call()

        assertEquals( 1, tagList.size() )
        assertEquals( 'refs/tags/0.2.3', tagList.get( 0 ).getName() )
    }

    @Test
    void 'tag is rolled back when push fails'() {
        when:
        tagReleaseTask.configure()

        try {
            tagReleaseTask.execute()
            fail()
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        def tagList = new Git( gradleRepository ).tagList().call()
        assertEquals( 0, tagList.size() )
    }

}
