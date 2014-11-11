package be.xvrt.gradle.release.plugin

class CommitReleaseTask extends AbstractScmTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.COMMIT_RELEASE_TASK}"

    CommitReleaseTask() {
        super( ReleasePlugin.COMMIT_RELEASE_TASK )
    }

    @Override
    void configure() {
    }

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info( "${LOG_TAG} committing release skipped because SCM support is disabled." )
        }
        else {
            def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK

            def scmRemote = extension.getAt ReleasePluginExtension.SCM_REMOTE
            def commitMessage = extension.getAt ReleasePluginExtension.RELEASE_COMMIT_MSG
            def releaseVersion = project.version

            commit commitMessage, releaseVersion
            push scmRemote
        }
    }

    @Override
    void rollback( Exception exception ) {
        throw exception;
    }

}
