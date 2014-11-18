package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.Commit
import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.task.AbstractScmTask

class CommitReleaseTask extends AbstractScmTask {

    private Commit commitId

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

        commitId = commit commitMessage, releaseVersion
        push scmRemote
    }

    @Override
    void rollback( Exception exception ) {
        rollbackCommit()

        throw exception;
    }

    private void rollbackCommit() throws ScmException {
        if ( commitId ) {
            logger.info "${name} rolling back commit due to error."

            getScmHelper().deleteCommit commitId
        }
    }

}
