package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.util.GradleProperties
import org.gradle.api.tasks.TaskAction

class PrepareNextReleaseTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.PREPARE_NEXT_RELEASE_TASK} "

    String releasedVersion
    String nextVersion

    @Override
    def configure() {
    }

    @TaskAction
    def setNextVersion() {
        releasedVersion = project.version
        nextVersion = buildNextVersion releasedVersion

        logger.info( LOG_TAG + "setting next version to ${nextVersion}." )
        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion( nextVersion )
    }

    @Override
    def rollback() {
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
