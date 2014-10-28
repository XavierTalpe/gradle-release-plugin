package be.xvrt.gradle.release.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph

class ReleasePlugin implements Plugin<Project> {

    public static final String PREPARE_RELEASE_TASK = 'prepareRelease'
    public static final String TAG_RELEASE_TASK = 'tagRelease'
    public static final String PREPARE_NEXT_RELEASE_TASK = 'prepareNextRelease'
    public static final String RELEASE_TASK = 'release'

    public static final String RELEASE_GROUP = 'release'

    private Task prepareReleaseTask
    private Task tagReleaseTask
    private Task prepareNextReleaseTask
    private Task releaseTask

    private ReleasePluginExtension extension

    void apply( Project project ) {
        createExtension project
        createTasks project

        project.afterEvaluate {
            setBuildTaskDependencies project
        }

        project.gradle.taskGraph.whenReady {
            ensureTaskConfigurationIsRun project.gradle.taskGraph
        }
    }

    private void createExtension( Project project ) {
        extension = project.extensions.create( RELEASE_TASK, ReleasePluginExtension, project )
    }

    private void createTasks( Project project ) {
        prepareReleaseTask = project.tasks.create( PREPARE_RELEASE_TASK, PrepareReleaseTask )
        tagReleaseTask = project.tasks.create( TAG_RELEASE_TASK, TagReleaseTask )
        prepareNextReleaseTask = project.tasks.create( PREPARE_NEXT_RELEASE_TASK, PrepareNextReleaseTask )
        releaseTask = project.tasks.create( RELEASE_TASK, ReleaseTask )

        prepareReleaseTask.group = RELEASE_GROUP
        prepareReleaseTask.description = 'TODO'

        tagReleaseTask.group = RELEASE_GROUP
        tagReleaseTask.description = 'TODO'
        tagReleaseTask.dependsOn prepareReleaseTask

        prepareNextReleaseTask.group = RELEASE_GROUP
        prepareNextReleaseTask.description = 'TODO'
        prepareNextReleaseTask.dependsOn tagReleaseTask

        releaseTask.group = RELEASE_GROUP
        releaseTask.description = 'TODO'
        releaseTask.dependsOn prepareReleaseTask, tagReleaseTask, prepareNextReleaseTask
    }

    private void setBuildTaskDependencies( Project project ) {
        def buildTask = project.tasks.findByName 'build'
        if ( buildTask ) {
            releaseTask.dependsOn buildTask
            tagReleaseTask.dependsOn buildTask
            prepareNextReleaseTask.dependsOn buildTask

            // Using mustRunAfter ensures that prepareRelease is executed first.
            // It also prevents build from automatically executing prepareRelease even
            // when release is not marked for execution. This is unlike dependsOn.
            buildTask.mustRunAfter prepareReleaseTask
        }
    }

    private void ensureTaskConfigurationIsRun( TaskExecutionGraph taskGraph ) {
        if ( taskGraph.hasTask( prepareReleaseTask ) ) {
            prepareReleaseTask.configure()
        }

        if ( taskGraph.hasTask( tagReleaseTask ) ) {
            tagReleaseTask.configure()
        }
    }

}
