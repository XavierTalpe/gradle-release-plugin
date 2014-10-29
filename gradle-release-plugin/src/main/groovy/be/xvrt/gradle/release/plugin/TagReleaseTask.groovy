package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmHelper
import be.xvrt.gradle.release.plugin.scm.ScmHelperFactory

class TagReleaseTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.TAG_RELEASE_TASK}"

    private ScmHelper scmHelper

    @Override
    void configure() {
        def releaseExtensions = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )
        scmHelper = ScmHelperFactory.create( releaseExtensions.scmRootDir )
    }

    @Override
    void run() {
        def releaseExtensions = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )

        def scmRemote = releaseExtensions.scmRemote
        def tagMessage = releaseExtensions.tagMessage
        def commitMessage = releaseExtensions.commitMessage
        def releaseVersion = project.version

        commit( commitMessage, releaseVersion )
        tag( releaseVersion, tagMessage )
        push( scmRemote )
    }

    private void commit( commitMessage, releaseVersion ) {
        logger.info( "${LOG_TAG} committing release to SCM." )
        scmHelper.commit commitMessage + releaseVersion
    }

    private void tag( releaseVersion, tagMessage ) {
        logger.info( "${LOG_TAG} tagging release to SCM." )
        scmHelper.tag releaseVersion, tagMessage + releaseVersion
    }

    private push( scmRemote ) {
        logger.info( "${LOG_TAG} pushing local changes to ${scmRemote}" )
        scmHelper.push scmRemote
    }

    @Override
    void rollback( Exception exception ) {
    }

}
