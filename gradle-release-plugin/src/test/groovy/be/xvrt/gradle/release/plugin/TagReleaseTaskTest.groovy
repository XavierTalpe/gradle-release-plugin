package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertNotNull

class TagReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Project project
    private Task tagReleaseTask

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin

        tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
    }

    @org.junit.Test
    public void testConventions() throws Exception {
        def scmRootDir = tagReleaseTask.convention.findByName( ReleasePluginConvention.SCM_ROOT_DIR )
        assertNotNull( scmRootDir )
    }

}
