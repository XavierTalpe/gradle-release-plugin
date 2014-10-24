package be.xvrt.gradle.release.plugin

import org.gradle.api.tasks.TaskAction

class PrepareReleaseTask extends RollbackTask {

    private static final GString TASK_TAG = ":${ReleasePlugin.PREPARE_RELEASE_TASK} "

    private String originalVersion
    private String releaseVersion

    @Override
    def configure() {
        originalVersion = project.version
        releaseVersion = buildReleaseVersion()

        // TODO: Optionally write to file
        project.version = releaseVersion
        logger.info( TASK_TAG + "set release version to ${project.version}." )
    }

    @TaskAction
    def executeTask() {

    }

    @Override
    def rollback() {

    }

    boolean wasSnapshotVersion() {
        return originalVersion.endsWith( '-SNAPSHOT' )
    }

    private String buildReleaseVersion() {
        def releaseVersion = originalVersion

        if ( releaseVersion.endsWith( '-SNAPSHOT' ) ) {
            releaseVersion -= '-SNAPSHOT'
        }

        releaseVersion
    }

}
