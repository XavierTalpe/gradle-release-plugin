package be.xvrt.gradle.release

import org.gradle.api.Plugin
import org.gradle.api.Project

class ReleasePlugin implements Plugin<Project> {

    static final String PREPARE_RELEASE_TASK = 'prepareRelease'
    static final String RELEASE_TASK = 'release'
    static final String SAVE_RELEASE_TASK = 'saveRelease'

    static final String TASK_GROUP = 'release';

    void apply( Project project ) {
        def prepareReleaseTask = project.tasks.create( PREPARE_RELEASE_TASK, PrepareReleaseTask )
        def releaseTask = project.tasks.create( RELEASE_TASK, ReleaseTask )
        def saveReleaseTask = project.tasks.create( SAVE_RELEASE_TASK, SaveReleaseTask )

        prepareReleaseTask.group = TASK_GROUP
        prepareReleaseTask.description = 'TODO'

        releaseTask.group = TASK_GROUP
        releaseTask.description = 'TODO'
        releaseTask.dependsOn( prepareReleaseTask, 'build', saveReleaseTask )

        saveReleaseTask.group = TASK_GROUP
        saveReleaseTask.description = 'TODO'
    }

}
