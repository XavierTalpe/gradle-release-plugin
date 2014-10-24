package be.xvrt.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

class ReleasePluginTest {

    private Project project

    @Before
    public void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin
    }

    @Test
    public void testAllTasksAddedToProject() {
        assertTrue( project.tasks.prepareRelease instanceof PrepareReleaseTask)
        assertTrue( project.tasks.release instanceof ReleaseTask )
        assertTrue( project.tasks.saveRelease instanceof SaveReleaseTask )
    }

}
