package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.properties.GradleProperties
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PrepareReleaseTask extends DefaultTask {

    boolean isSnapshotVersion
    GradleProperties gradleProperties

    @TaskAction
    def prepareReleaseVersion() {
        def releaseVersion = buildReleaseVersion()
        gradleProperties.setVersion( releaseVersion )
        logger.debug( "Project version set to ${releaseVersion}." )
    }

    private String buildReleaseVersion() {
        logger.debug( 'Preparing for release.' )
        def projectVersion = getProjectVersion()

        if ( projectVersion.endsWith( '-SNAPSHOT' ) ) {
            projectVersion -= '-SNAPSHOT'
            isSnapshotVersion = true
        }

        projectVersion
    }

    private String getProjectVersion() {
        project.version
    }


}
