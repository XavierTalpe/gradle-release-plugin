package be.xvrt.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PrepareReleaseTask extends DefaultTask {

    @TaskAction
    def setReleaseVersion() {
        String releaseVersion = project.version

        if ( releaseVersion.endsWith( '-SNAPSHOT' ) ) {
            releaseVersion -= '-SNAPSHOT';
        }

        project.version = releaseVersion

        logger.quiet( 'An info log message which is always logged.' )
        logger.error( 'An error log message.' )
        logger.warn( 'A warning log message.' )
        logger.lifecycle( 'A lifecycle info log message.' )
        logger.info( 'An info log message.' )
        logger.debug( 'A debug log message.' )
        logger.trace( 'A trace log message.' )
    }

}
