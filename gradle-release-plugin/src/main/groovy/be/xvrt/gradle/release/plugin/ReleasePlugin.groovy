package be.xvrt.gradle.release.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph

class ReleasePlugin implements Plugin<Project> {

    static final String PREPARE_RELEASE_TASK = 'prepareRelease'
    static final String RELEASE_TASK = 'release'
    static final String TAG_RELEASE_TASK = 'tagRelease'

    static final String TASK_GROUP = 'release'

    private Task prepareReleaseTask
    private Task releaseTask
    private Task tagReleaseTask

    void apply( Project project ) {
        createTasks( project )

        project.afterEvaluate {
            setTaskDependencies( project )
        }

        project.gradle.taskGraph.whenReady {
            ensureTaskConfigurationIsRun( project.gradle.taskGraph )
        }
    }

    private void createTasks( Project project ) {
        prepareReleaseTask = project.tasks.create( PREPARE_RELEASE_TASK, PrepareReleaseTask )
        releaseTask = project.tasks.create( RELEASE_TASK, ReleaseTask )
        tagReleaseTask = project.tasks.create( TAG_RELEASE_TASK, TagReleaseTask )

        prepareReleaseTask.group = TASK_GROUP
        prepareReleaseTask.description = 'TODO'

        releaseTask.group = TASK_GROUP
        releaseTask.description = 'TODO'
        releaseTask.dependsOn prepareReleaseTask

        tagReleaseTask.group = TASK_GROUP
        tagReleaseTask.description = 'TODO'
        tagReleaseTask.dependsOn releaseTask
    }

    private void setTaskDependencies( Project project ) {
        def buildTask = project.tasks.findByName( 'build' )
        if ( buildTask ) {
            releaseTask.dependsOn( buildTask )

            // Using must run after ensures that prepareRelease is executed first.
            // It also prevents build from automatically executing prepareRelease even
            // when release is not marked for execution. This is unlike dependsOn.
            buildTask.mustRunAfter prepareReleaseTask
        }
    }

    private void ensureTaskConfigurationIsRun( TaskExecutionGraph taskGraph ) {
        if ( taskGraph.hasTask( prepareReleaseTask ) ) {
            prepareReleaseTask.configure()
        }
    }

}
