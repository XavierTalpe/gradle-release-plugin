package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.Commit
import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.task.AbstractScmTask

class CommitReleaseTask extends AbstractScmTask {

    private Commit commitId

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info ":${name} skipping commitRelease because SCM support is disabled."
        }
        else {
            commitId = commit()
            push()
        }
    }

    private Commit commit() {
        def releaseVersion = project.version
        def commitMessage = extension.getAt ReleasePluginExtension.RELEASE_COMMIT_MSG

        commit commitMessage, releaseVersion
    }

    @Override
    void rollback( Exception exception ) {
        rollbackCommit commitId
    }

    private void rollbackCommit( Commit commitId ) throws ScmException {
        if ( commitId ) {
            logger.info ":${name} rolling back commit due to error."

            scmHelper.deleteCommit commitId
        }
    }

}
