package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmHelper
import be.xvrt.gradle.release.plugin.scm.ScmHelperFactory
import be.xvrt.gradle.release.plugin.util.GradleProperties

class UpdateVersionTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.UPDATE_VERSION_TASK}"

    String releasedVersion
    String nextVersion

    @Override
    void configure() {
    }

    @Override
    void run() {
        releasedVersion = project.version
        nextVersion = buildNextVersion releasedVersion

        saveVersion nextVersion
        commitChanges nextVersion
    }

    @Override
    void rollback( Exception exception ) {
        throw exception;
    }

    private String buildNextVersion( String version ) {
        def extension = project.extensions.getByName ReleasePluginExtension.NAME
        def nextVersionClosure = extension.getAt ReleasePluginExtension.NEXT_VERSION

        def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        def wasSnapshotVersion = prepareReleaseTask.wasSnapshotVersion()

        nextVersionClosure version, wasSnapshotVersion
    }

    private void saveVersion( String nextVersion ) {
        logger.info( "${LOG_TAG} setting next version to ${nextVersion}." )

        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion( nextVersion )
    }

    private void commitChanges( String nextVersion ) {
        def extension = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )
        def scmRemote = extension.getAt( ReleasePluginExtension.SCM_REMOTE )
        def prepareMessage = extension.getAt( ReleasePluginExtension.PREPARE_MSG )

        def scmHelper = getScmHelper()

        logger.info( "${LOG_TAG} committing release to SCM." )
        scmHelper.commit prepareMessage + nextVersion

        logger.info( "${LOG_TAG} pushing local changes to ${scmRemote}" )
        scmHelper.push scmRemote
    }

    private ScmHelper getScmHelper() {
        def extension = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )
        def scmRootDir = extension.getAt( ReleasePluginExtension.SCM_ROOT_DIR )

        ScmHelperFactory.create scmRootDir
    }

}
