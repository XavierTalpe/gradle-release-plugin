package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmHelper
import be.xvrt.gradle.release.plugin.scm.ScmHelperFactory

// TODO: Write test?
abstract class AbstractScmTask extends RollbackTask {

    private final GString logTag

    protected AbstractScmTask( String taskName ) {
        logTag = ":${taskName}"
    }

    protected final boolean isScmSupportDisabled() {
        def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK
        extension.getAt ReleasePluginExtension.SCM_DISABLED
    }

    protected final void commit( String commitMessage, String releaseVersion ) {
        logger.info( "${logTag} committing release." )

        commitMessage = injectVersion commitMessage, releaseVersion

        getScmHelper().commit commitMessage
    }

    protected final void tag( String tagName, String tagMessage, String releaseVersion ) {
        logger.info( "${logTag} tagging release." )

        tagName = injectVersion tagName, releaseVersion
        tagMessage = injectVersion tagMessage, releaseVersion

        getScmHelper().tag tagName, tagMessage
    }

    protected final push( String scmRemote ) {
        logger.info "${logTag} pushing local changes to ${scmRemote}"

        getScmHelper().push scmRemote
    }

    private ScmHelper getScmHelper() {
        def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK
        def scmRootDir = extension.getAt ReleasePluginExtension.SCM_ROOT_DIR

        ScmHelperFactory.create scmRootDir
    }

    private String injectVersion( String input, String version ) {
        input.replaceAll( '%version', version )
    }

}
