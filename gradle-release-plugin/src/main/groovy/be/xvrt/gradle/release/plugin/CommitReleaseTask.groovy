package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmException

class CommitReleaseTask extends AbstractScmTask {

    private static final GString LOG_TAG = ":${ReleasePlugin.COMMIT_RELEASE_TASK}"

    @Override
    void configure() {
    }

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info "${LOG_TAG} skipping commitRelease because SCM support is disabled."
        }
        else {
            commitChanges()
        }
    }

    private void commitChanges() {
        def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK

        def releaseVersion = project.version
        def commitMessage = extension.getAt ReleasePluginExtension.RELEASE_COMMIT_MSG
        def scmRemote = extension.getAt ReleasePluginExtension.SCM_REMOTE

        commit commitMessage, releaseVersion
        push scmRemote
    }

    @Override
    void rollback( Exception exception ) {
        rollbackCommit()

        throw exception;
    }

    private void commit( String commitMessage, String releaseVersion ) throws ScmException {
        logger.info "${LOG_TAG} committing local changes."

        commitMessage = injectVersion commitMessage, releaseVersion

        getScmHelper().commit commitMessage
    }

    private void push( String scmRemote ) throws ScmException {
        logger.info "${LOG_TAG} pushing local commit to ${scmRemote}."

        getScmHelper().push scmRemote
    }

    private void rollbackCommit() throws ScmException {
        logger.info "${LOG_TAG} rolling back commit due to error."

        getScmHelper().rollbackLastCommit()
    }

}
