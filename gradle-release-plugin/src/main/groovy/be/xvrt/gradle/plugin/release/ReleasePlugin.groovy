package be.xvrt.gradle.plugin.release
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph

class ReleasePlugin implements Plugin<Project> {

    public static final String PREPARE_RELEASE_TASK = 'prepareRelease'
    public static final String COMMIT_RELEASE_TASK = 'commitRelease'
    public static final String TAG_RELEASE_TASK = 'tagRelease'
    public static final String UPDATE_VERSION_TASK = 'updateVersion'
    public static final String RELEASE_TASK = 'release'

    public static final String RELEASE_GROUP = 'release'

    private PrepareReleaseTask prepareReleaseTask
    private CommitReleaseTask commitReleaseTask
    private TagReleaseTask tagReleaseTask
    private UpdateVersionTask updateVersionTask
    private ReleaseTask releaseTask

    void apply( Project project ) {
        project.extensions.create( ReleasePluginExtension.NAME, ReleasePluginExtension, project )

        createTasks project

        project.afterEvaluate {
            setBuildTaskDependencies project
        }

        project.gradle.taskGraph.whenReady {
            ensureTaskConfigurationIsRun project.gradle.taskGraph
        }
    }

    private void createTasks( Project project ) {
        prepareReleaseTask = project.tasks.create( PREPARE_RELEASE_TASK, PrepareReleaseTask )
        commitReleaseTask = project.tasks.create( COMMIT_RELEASE_TASK, CommitReleaseTask )
        tagReleaseTask = project.tasks.create( TAG_RELEASE_TASK, TagReleaseTask )
        updateVersionTask = project.tasks.create( UPDATE_VERSION_TASK, UpdateVersionTask )
        releaseTask = project.tasks.create( RELEASE_TASK, ReleaseTask )

        prepareReleaseTask.group = RELEASE_GROUP
        prepareReleaseTask.description = 'Checks for SNAPSHOT dependencies and sets the release version before the build is started.'

        commitReleaseTask.group = RELEASE_GROUP
        commitReleaseTask.description = 'Commits any file changes for this release to the SCM.'
        commitReleaseTask.dependsOn prepareReleaseTask

        tagReleaseTask.group = RELEASE_GROUP
        tagReleaseTask.description = 'Tags this release to the SCM.'
        tagReleaseTask.dependsOn commitReleaseTask

        updateVersionTask.group = RELEASE_GROUP
        updateVersionTask.description = 'Sets the version for the next snapshot build and commits this change to the SCM.'
        updateVersionTask.dependsOn tagReleaseTask

        releaseTask.group = RELEASE_GROUP
        releaseTask.description = 'Parent task of this plugin. Ensures all other tasks are executed at the right time.'
        releaseTask.dependsOn prepareReleaseTask, commitReleaseTask, tagReleaseTask, updateVersionTask
    }

    private void setBuildTaskDependencies( Project project ) {
        def buildTask = project.tasks.findByName 'build'
        if ( buildTask ) {
            releaseTask.dependsOn buildTask
            commitReleaseTask.dependsOn buildTask
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
    }

}
