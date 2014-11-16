package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmException

class CommitReleaseTask extends AbstractScmTask {

    @Override
    void configure() {
    }

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info "${name} skipping commitRelease because SCM support is disabled."
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

    private void rollbackCommit() throws ScmException {
        logger.info "${name} rolling back commit due to error."

        getScmHelper().rollbackLastCommit()
    }

}
