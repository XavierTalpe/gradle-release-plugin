package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

class ReleasePluginTest {

    private Project project

    @Before
    void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin
    }

    @Test
    void testAllTasksAddedToProject() {
        assertTrue( project.tasks.prepareRelease instanceof PrepareReleaseTask )
        assertTrue( project.tasks.release instanceof ReleaseTask )
        assertTrue( project.tasks.tagRelease instanceof TagReleaseTask )
    }

}