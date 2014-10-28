package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

class ReleasePluginTest {

    private Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin
    }

    @Test
    void testAllTasksAddedToProject() {
        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        def tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
        def prepareNextReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_NEXT_RELEASE_TASK
        def releaseTask = project.tasks.getByName ReleasePlugin.RELEASE_TASK

        assertTrue( prepareReleaseTask instanceof PrepareReleaseTask )
        assertTrue( tagReleaseTask instanceof TagReleaseTask )
        assertTrue( prepareNextReleaseTask instanceof PrepareNextReleaseTask )
        assertTrue( releaseTask instanceof ReleaseTask )

        assertTrue( tagReleaseTask.dependsOn.contains( prepareReleaseTask ) )
        assertTrue( prepareNextReleaseTask.dependsOn.contains( tagReleaseTask ) )

        assertTrue( releaseTask.dependsOn.contains( prepareReleaseTask ) )
        assertTrue( releaseTask.dependsOn.contains( tagReleaseTask ) )
        assertTrue( releaseTask.dependsOn.contains( prepareNextReleaseTask ) )
    }

    @Test
    void testEnsurePrepareReleaseIsRunBeforeBuild() {
        setup:
        def buildTask = project.tasks.create 'build'
        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK

        when:
        project.evaluate()

        then:
        buildTask.mustRunAfter.each {
            task -> task == prepareReleaseTask
        }
    }

    @Test
    void testEnsureReleaseDependsOnBuild() {
        setup:
        def buildTask = project.tasks.create 'build'
        def tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
        def prepareNextReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_NEXT_RELEASE_TASK
        def releaseTask = project.tasks.getByName ReleasePlugin.RELEASE_TASK

        when:
        project.evaluate()

        then:
        releaseTask.dependsOn.contains buildTask
        tagReleaseTask.dependsOn.contains buildTask
        prepareNextReleaseTask.dependsOn.contains buildTask
    }

}
