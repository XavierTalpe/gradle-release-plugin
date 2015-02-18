package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.Commit
import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.task.PluginScmTask

class UpdateVersionTask extends PluginScmTask {

    String releasedVersion
    String nextVersion

    private Commit commitId

    @Override
    void run() {
        releasedVersion = projectVersion
        nextVersion = buildNextVersion releasedVersion

        saveVersion nextVersion

        if ( isScmSupportDisabled() ) {
            logger.info ":${name} skipping updateVersion commit because SCM support is disabled."
        }
        else {
            commitId = commit nextVersion
            push()
        }
    }

    @Override
    void rollback( Exception exception ) {
        rollbackCommit commitId
    }

    private String buildNextVersion( String version ) {
        // Allow user to directly specify the next version from the
        // command line using -PnextVersion=XXX.
        // This takes precedence over the properties extension.
        if ( project.hasProperty( ReleasePluginExtension.NEXT_VERSION ) ) {
            project.property ReleasePluginExtension.NEXT_VERSION
        }
        else {
            def nextVersionClosure = extension.getAt( ReleasePluginExtension.NEXT_VERSION ) as Closure<String>

            def prepareReleaseTask = project.tasks.getByName( ReleasePlugin.PREPARE_RELEASE_TASK ) as PrepareReleaseTask
            def wasSnapshotVersion = prepareReleaseTask.wasSnapshotVersion()

            nextVersionClosure version, wasSnapshotVersion
        }
    }

    private void saveVersion( String nextVersion ) {
        logger.info( ":${name} setting next version to ${nextVersion}." )

        setProjectVersion( nextVersion, true )
    }

    private Commit commit( String nextVersion ) {
        def updateVersionMessage = extension.getAt( ReleasePluginExtension.UPDATE_VERSION_COMMIT_MSG ) as String

        commit updateVersionMessage, nextVersion
    }

    private void rollbackCommit( Commit commitId ) throws ScmException {
        if ( commitId ) {
            logger.info ":${name} rolling back unpushed commit due to error."

            scmHelper.deleteCommit commitId
        }
    }

}
