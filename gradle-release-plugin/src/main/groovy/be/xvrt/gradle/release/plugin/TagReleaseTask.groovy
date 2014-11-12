package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmException

class TagReleaseTask extends AbstractScmTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.TAG_RELEASE_TASK}"

    @Override
    void configure() {
    }

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info( "${LOG_TAG} tagging release skipped because SCM support is disabled." )
        }
        else {
            def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK

            def scmRemote = extension.getAt ReleasePluginExtension.SCM_REMOTE
            def tagName = extension.getAt ReleasePluginExtension.RELEASE_TAG
            def tagMessage = extension.getAt ReleasePluginExtension.RELEASE_TAG_MSG
            def releaseVersion = project.version

            tag tagName, tagMessage, releaseVersion
            push scmRemote
        }
    }

    private void tag( String tagName, String tagMessage, String releaseVersion ) throws ScmException {
        logger.info( "${LOG_TAG} tagging release." )

        tagName = injectVersion tagName, releaseVersion
        tagMessage = injectVersion tagMessage, releaseVersion

        getScmHelper().tag tagName, tagMessage
    }

    private void push( String scmRemote ) throws ScmException {
        logger.info "${LOG_TAG} pushing local changes to ${scmRemote}"

        getScmHelper().push scmRemote
    }

    @Override
    void rollback( Exception exception ) {
        exception.printStackTrace()
        throw exception;
    }

}
