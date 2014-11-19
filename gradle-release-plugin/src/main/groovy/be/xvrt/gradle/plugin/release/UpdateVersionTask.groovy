package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.Commit
import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.util.GradleProperties
import be.xvrt.gradle.plugin.task.AbstractScmTask

class UpdateVersionTask extends AbstractScmTask {

    String releasedVersion
    String nextVersion

    private Commit commitId

    @Override
    void run() {
        releasedVersion = project.version
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

        throw exception
    }

    private String buildNextVersion( String version ) {
        // Allow user to directly specify the next version from the
        // command line using -PnextVersion=XXX. This takes
        // precedence over executing the closure.
        if ( project.hasProperty( ReleasePluginExtension.NEXT_VERSION ) ) {
            project.property ReleasePluginExtension.NEXT_VERSION
        }
        else {
            def extension = project.extensions.getByName ReleasePluginExtension.NAME
            def nextVersionClosure = extension.getAt ReleasePluginExtension.NEXT_VERSION

            def prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
            def wasSnapshotVersion = prepareReleaseTask.wasSnapshotVersion()

            nextVersionClosure version, wasSnapshotVersion
        }
    }

    private void saveVersion( String nextVersion ) {
        logger.info( ":${name} setting next version to ${nextVersion}." )

        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion nextVersion
    }

    private Commit commit( String nextVersion ) {
        def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK
        def updateVersionMessage = extension.getAt ReleasePluginExtension.UPDATE_VERSION_COMMIT_MSG

        commit updateVersionMessage, nextVersion
    }

    private void rollbackCommit( Commit commitId ) throws ScmException {
        if ( commitId ) {
            logger.info ":${name} rolling back commit due to error."

            scmHelper.deleteCommit commitId
        }
    }

}
