package be.xvrt.gradle.release.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph

class ReleasePlugin implements Plugin<Project> {

    public static final String PREPARE_RELEASE_TASK = 'prepareRelease'
    public static final String TAG_RELEASE_TASK = 'tagRelease'
    public static final String UPDATE_VERSION_TASK = 'updateVersion'
    public static final String RELEASE_TASK = 'release'

    public static final String RELEASE_GROUP = 'release'

    private Task prepareReleaseTask
    private Task tagReleaseTask
    private Task updateVersionTask
    private Task releaseTask

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
        project.extensions.create( PREPARE_RELEASE_TASK, PrepareReleaseTaskExtension )
        project.extensions.create( UPDATE_VERSION_TASK, UpdateVersionTaskExtension )
        project.extensions.create( RELEASE_TASK, ReleasePluginExtension, project )
    }

    private void createTasks( Project project ) {
        prepareReleaseTask = project.tasks.create( PREPARE_RELEASE_TASK, PrepareReleaseTask )
        tagReleaseTask = project.tasks.create( TAG_RELEASE_TASK, TagReleaseTask )
        updateVersionTask = project.tasks.create( UPDATE_VERSION_TASK, UpdateVersionTask )
        releaseTask = project.tasks.create( RELEASE_TASK, ReleaseTask )

        prepareReleaseTask.group = RELEASE_GROUP
        prepareReleaseTask.description = 'Sets the release version before the release build is started.'

        tagReleaseTask.group = RELEASE_GROUP
        tagReleaseTask.description = 'TODO'
        tagReleaseTask.dependsOn prepareReleaseTask

        updateVersionTask.group = RELEASE_GROUP
        updateVersionTask.description = 'TODO'
        updateVersionTask.dependsOn tagReleaseTask

        releaseTask.group = RELEASE_GROUP
        releaseTask.description = 'Parent task of this plugin. Ensures all other tasks are executed at the right time.'
        releaseTask.dependsOn prepareReleaseTask, tagReleaseTask, updateVersionTask
    }

    private void setBuildTaskDependencies( Project project ) {
        def buildTask = project.tasks.findByName 'build'
        if ( buildTask ) {
            releaseTask.dependsOn buildTask
            tagReleaseTask.dependsOn buildTask
            updateVersionTask.dependsOn buildTask

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

        if ( taskGraph.hasTask( updateVersionTask ) ) {
            updateVersionTask.configure()
        }
    }

}
