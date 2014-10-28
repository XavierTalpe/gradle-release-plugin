package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmHelper
import be.xvrt.gradle.release.plugin.scm.ScmHelperFactory

class TagReleaseTask extends RollbackTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.TAG_RELEASE_TASK} "

    private ScmHelper scmHelper

    @Override
    void configure() {
        def releaseExtensions = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )

        scmHelper = ScmHelperFactory.create( releaseExtensions.scmRootDir )
    }

    @Override
    void run() {
        def releaseExtensions = project.extensions.getByName( ReleasePlugin.RELEASE_TASK )

        def commitMessage = releaseExtensions.commitMessage
        def tagMessage = releaseExtensions.tagMessage
        def releaseVersion = project.version

        logger.info( LOG_TAG + "committing release to SCM." )
        scmHelper.commit( commitMessage + releaseVersion )

        logger.info( LOG_TAG + "tagging release to SCM." )
        scmHelper.tag( releaseVersion, tagMessage + releaseVersion )
    }

    @Override
    void rollback( Exception exception ) {
    }

}
