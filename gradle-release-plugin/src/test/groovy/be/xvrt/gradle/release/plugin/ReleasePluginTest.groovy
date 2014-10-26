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
        def prepareReleaseTask = project.tasks.findByName( ReleasePlugin.PREPARE_RELEASE_TASK )
        def releaseTask = project.tasks.findByName( ReleasePlugin.RELEASE_TASK )

        assertTrue( prepareReleaseTask instanceof PrepareReleaseTask )
        assertTrue( releaseTask instanceof ReleaseTask )

        assertTrue( releaseTask.dependsOn.contains( prepareReleaseTask ) )
    }

    @Test
    void testEnsurePrepareReleaseIsRunBeforeBuild() {
        setup:
        def buildTask = project.tasks.create 'build'
        def prepareReleaseTask = project.tasks.findByName( ReleasePlugin.PREPARE_RELEASE_TASK )

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
        def releaseTask = project.tasks.findByName ReleasePlugin.RELEASE_TASK

        when:
        project.evaluate()

        then:
        releaseTask.dependsOn.contains buildTask
    }

}
