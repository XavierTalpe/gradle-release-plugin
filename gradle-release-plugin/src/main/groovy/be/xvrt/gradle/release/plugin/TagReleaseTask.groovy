package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmHelper
import be.xvrt.gradle.release.plugin.scm.ScmHelperFactory

class TagReleaseTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.TAG_RELEASE_TASK}"

    @Override
    void configure() {
    }

    @Override
    void run() {
        def extension = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )
        def scmRemote = extension.getAt( ReleasePluginExtension.SCM_REMOTE )
        def tagMessage = extension.getAt( ReleasePluginExtension.TAG_MSG )
        def commitMessage = extension.getAt( ReleasePluginExtension.COMMIT_MSG )
        def releaseVersion = project.version

        def scmHelper = getScmHelper()
        commit scmHelper, commitMessage, releaseVersion
        tag scmHelper, releaseVersion, tagMessage
        push scmHelper, scmRemote
    }

    private void commit( ScmHelper scmHelper, String commitMessage, String releaseVersion ) {
        logger.info( "${LOG_TAG} committing release to SCM." )
        scmHelper.commit commitMessage + releaseVersion
    }

    private void tag( ScmHelper scmHelper, String releaseVersion, String tagMessage ) {
        logger.info( "${LOG_TAG} tagging release to SCM." )
        scmHelper.tag releaseVersion, tagMessage + releaseVersion
    }

    private push( ScmHelper scmHelper, String scmRemote ) {
        logger.info( "${LOG_TAG} pushing local changes to ${scmRemote}" )
        scmHelper.push scmRemote
    }

    @Override
    void rollback( Exception exception ) {
        throw exception;
    }

    private ScmHelper getScmHelper() {
        def extension = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )
        def scmRootDir = extension.getAt( ReleasePluginExtension.SCM_ROOT_DIR )

        ScmHelperFactory.create scmRootDir
    }

}
