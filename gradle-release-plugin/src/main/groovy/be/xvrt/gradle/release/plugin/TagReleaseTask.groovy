package be.xvrt.gradle.release.plugin

class TagReleaseTask extends AbstractScmTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.TAG_RELEASE_TASK}"

    @Override
    void configure() {
    }

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info "${LOG_TAG} skipping tagRelease because SCM support is disabled."
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

    @Override
    void rollback( Exception exception ) {
        // TODO #6 Rollback changes
        throw exception;
    }

}
