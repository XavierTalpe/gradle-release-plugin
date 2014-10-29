package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmHelper
import be.xvrt.gradle.release.plugin.scm.ScmHelperFactory
import be.xvrt.gradle.release.plugin.util.GradleProperties

class PrepareNextReleaseTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.PREPARE_NEXT_RELEASE_TASK}"

    String releasedVersion
    String nextVersion

    private ScmHelper scmHelper

    @Override
    void configure() {
        def releaseExtensions = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )
        scmHelper = ScmHelperFactory.create( releaseExtensions.scmRootDir )
    }

    @Override
    void run() {
        releasedVersion = project.version
        nextVersion = buildNextVersion releasedVersion

        incrementVersion( nextVersion )
        commitChanges( nextVersion )
    }

    private void incrementVersion( String nextVersion ) {
        logger.info( "${LOG_TAG} setting next version to ${nextVersion}." )

        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion( nextVersion )
    }

    private void commitChanges( String nextVersion ) {
        def releaseExtensions = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )
        def scmRemote = releaseExtensions.scmRemote
        def prepareMessage = releaseExtensions.prepareMessage

        logger.info( "${LOG_TAG} committing release to SCM." )
        scmHelper.commit prepareMessage + nextVersion

        logger.info( "${LOG_TAG} pushing local changes to ${scmRemote}" )
        scmHelper.push scmRemote
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
