package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.Commit
import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.task.PluginScmTask

class CommitReleaseTask extends PluginScmTask {

    private Commit commitId

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info ":${project.name}:${name} skipping commitRelease because SCM support is disabled."
        }
        else {
            commitId = commit()
            push()
        }
    }

    private Commit commit() {
        def releaseVersion = projectVersion
        def commitMessage = extension.getAt( ReleasePluginExtension.RELEASE_COMMIT_MSG ) as String

        commit commitMessage, releaseVersion
    }

    @Override
    void rollback( Exception exception ) {
        rollbackCommit commitId
    }

    private void rollbackCommit( Commit commitId ) throws ScmException {
        if ( commitId ) {
            logger.info ":${project.name}:${name} rolling back unpushed commit due to error."

            scmHelper.deleteCommit commitId
        }
    }

}
