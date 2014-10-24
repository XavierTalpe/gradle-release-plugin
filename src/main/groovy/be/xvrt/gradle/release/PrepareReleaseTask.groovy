package be.xvrt.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PrepareReleaseTask extends DefaultTask {

    boolean isSnapshotVersion

    @TaskAction
    def prepareReleaseVersion() {
        logger.debug( 'Preparing for release.' )
        String projectVersion = project.version

        if ( projectVersion.endsWith( '-SNAPSHOT' ) ) {
            projectVersion -= '-SNAPSHOT'
            isSnapshotVersion = true
        }

        project.version = projectVersion
        logger.debug( "Project version set to ${projectVersion}." )
    }

}
