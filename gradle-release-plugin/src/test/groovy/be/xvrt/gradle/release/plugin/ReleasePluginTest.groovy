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
        def commitReleaseTask = project.tasks.getByName ReleasePlugin.COMMIT_RELEASE_TASK
        def tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
        def updateVersionTask = project.tasks.getByName ReleasePlugin.UPDATE_VERSION_TASK
        def releaseTask = project.tasks.getByName ReleasePlugin.RELEASE_TASK

        assertTrue( prepareReleaseTask instanceof PrepareReleaseTask )
        assertTrue( commitReleaseTask instanceof CommitReleaseTask )
        assertTrue( tagReleaseTask instanceof TagReleaseTask )
        assertTrue( updateVersionTask instanceof UpdateVersionTask )
        assertTrue( releaseTask instanceof ReleaseTask )

        assertTrue( commitReleaseTask.dependsOn.contains( prepareReleaseTask ) )
        assertTrue( tagReleaseTask.dependsOn.contains( commitReleaseTask ) )
        assertTrue( updateVersionTask.dependsOn.contains( tagReleaseTask ) )

        assertTrue( releaseTask.dependsOn.contains( prepareReleaseTask ) )
        assertTrue( releaseTask.dependsOn.contains( tagReleaseTask ) )
        assertTrue( releaseTask.dependsOn.contains( updateVersionTask ) )
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
        def commitReleaseTask = project.tasks.getByName ReleasePlugin.COMMIT_RELEASE_TASK
        def tagReleaseTask = project.tasks.getByName ReleasePlugin.TAG_RELEASE_TASK
        def updateVersionTask = project.tasks.getByName ReleasePlugin.UPDATE_VERSION_TASK
        def releaseTask = project.tasks.getByName ReleasePlugin.RELEASE_TASK

        when:
        project.evaluate()

        then:
        assertTrue( releaseTask.dependsOn.contains( buildTask ) )
        assertTrue( commitReleaseTask.dependsOn.contains( buildTask ) )
        assertTrue( tagReleaseTask.dependsOn.contains( buildTask ) )
        assertTrue( updateVersionTask.dependsOn.contains( buildTask ) )
    }

}
