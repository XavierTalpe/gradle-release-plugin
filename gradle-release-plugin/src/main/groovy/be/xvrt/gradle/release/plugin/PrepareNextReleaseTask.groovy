package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.util.GradleProperties

class PrepareNextReleaseTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.PREPARE_NEXT_RELEASE_TASK} "

    String releasedVersion
    String nextVersion

    @Override
    void configure() {
    }

    @Override
    void run() {
        releasedVersion = project.version
        nextVersion = buildNextVersion releasedVersion

        logger.info( LOG_TAG + "setting next version to ${nextVersion}." )
        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion( nextVersion )
    }

    @Override
    void rollback( Exception exception ) {
    }

    private String buildNextVersion( String version ) {
        def lastDotIndex = version.findLastIndexOf { "." }
        def lastVersion = version.substring( lastDotIndex, version.length() )
        def incrementedVersionNumber = Integer.parseInt( lastVersion ) + 1

        def nextVersion = version.substring( 0, lastDotIndex ) + incrementedVersionNumber

        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        if ( prepareReleaseTask.wasSnapshotVersion() ) {
            nextVersion += '-SNAPSHOT'
        }

        nextVersion
    }

}
