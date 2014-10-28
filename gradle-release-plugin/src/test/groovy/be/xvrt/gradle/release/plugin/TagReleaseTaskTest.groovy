package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

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

}
