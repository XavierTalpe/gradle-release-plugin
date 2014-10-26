package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.util.GradleProperties

class PrepareReleaseTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.PREPARE_RELEASE_TASK} "

    String originalVersion
    String releaseVersion

    boolean wasSnapshotVersion() {
        return originalVersion.endsWith( '-SNAPSHOT' )
    }

    @Override
    def configure() {
        originalVersion = project.version
        releaseVersion = buildReleaseVersion originalVersion

        logger.info( LOG_TAG + "setting release version to ${project.version}." )
        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion( releaseVersion )
    }

    @Override
    def rollback() {
        // TODO
    }

    private String buildReleaseVersion( String version ) {
        if ( version.endsWith( '-SNAPSHOT' ) ) {
            version -= '-SNAPSHOT'
        }

        version
    }

}
