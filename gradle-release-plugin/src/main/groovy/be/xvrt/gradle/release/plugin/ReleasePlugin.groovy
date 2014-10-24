package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.properties.GradleProperties
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReleasePlugin implements Plugin<Project> {

    static final String PREPARE_RELEASE_TASK = 'prepareRelease'
    static final String RELEASE_TASK = 'release'
    static final String TAG_RELEASE_TASK = 'tagRelease'

    static final String TASK_GROUP = 'release';

    void apply( Project project ) {
        def prepareReleaseTask = project.tasks.create( PREPARE_RELEASE_TASK, PrepareReleaseTask )
        def releaseTask = project.tasks.create( RELEASE_TASK, ReleaseTask )
        def tagReleaseTask = project.tasks.create( TAG_RELEASE_TASK, TagReleaseTask )

        def gradleProperties = new GradleProperties( project )

        prepareReleaseTask.group = TASK_GROUP
        prepareReleaseTask.description = 'TODO'

        releaseTask.group = TASK_GROUP
        releaseTask.description = 'TODO'
        releaseTask.dependsOn prepareReleaseTask

        tagReleaseTask.group = TASK_GROUP
        tagReleaseTask.description = 'TODO'
        tagReleaseTask.gradleProperties = gradleProperties
        tagReleaseTask.dependsOn releaseTask

        project.afterEvaluate {
            def buildTask = project.tasks.findByName( 'build' )
            if ( buildTask ) {
                releaseTask.dependsOn( prepareReleaseTask, buildTask )
            }

            prepareReleaseTask.configure()
        }
    }

}
