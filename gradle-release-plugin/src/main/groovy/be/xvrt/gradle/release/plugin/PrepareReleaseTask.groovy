package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.util.GradleProperties

class PrepareReleaseTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.PREPARE_RELEASE_TASK}"

    String originalVersion
    String releaseVersion

    boolean wasSnapshotVersion() {
        return originalVersion.endsWith( '-SNAPSHOT' )
    }

    @Override
    void configure() {
        prepareReleaseVersion()
    }

    @Override
    void run() {
    }

    @Override
    void rollback( Exception exception ) {
    }

    private void prepareReleaseVersion() {
        originalVersion = project.version
        releaseVersion = buildReleaseVersion originalVersion

        logger.info( "${LOG_TAG} setting release version to ${releaseVersion}." )
        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion( releaseVersion )
    }

    private String buildReleaseVersion( String version ) {
        if ( version.endsWith( '-SNAPSHOT' ) ) {
            version -= '-SNAPSHOT'
        }

        version
    }

}
