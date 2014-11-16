package be.xvrt.gradle.plugin.task

import be.xvrt.gradle.plugin.release.ReleasePlugin
import be.xvrt.gradle.plugin.release.ReleasePluginExtension
import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.scm.ScmHelper
import be.xvrt.gradle.plugin.release.scm.ScmHelperFactory

abstract class AbstractScmTask extends AbstractDefaultTask {

    private scmHelper

    protected AbstractScmTask() {
    }

    protected final boolean isScmSupportDisabled() {
        def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK
        extension.getAt ReleasePluginExtension.SCM_DISABLED
    }

    protected final void commit( String commitMessage, String version ) throws ScmException {
        logger.info ":${name} committing local changes."

        commitMessage = injectVersion commitMessage, version

        getScmHelper().commit commitMessage
    }

    protected final void tag( String tagName, String tagMessage, String version ) throws ScmException {
        logger.info ":${name} tagging release."

        tagName = injectVersion tagName, version
        tagMessage = injectVersion tagMessage, version

        getScmHelper().tag tagName, tagMessage
    }

    protected final void push( String scmRemote ) throws ScmException {
        logger.info ":${name} pushing local changes to ${scmRemote}."

        getScmHelper().push scmRemote
    }

    protected final ScmHelper getScmHelper() {
        if ( scmHelper == null ) {
            def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK
            def scmRootDir = extension.getAt ReleasePluginExtension.SCM_ROOT_DIR

            scmHelper = ScmHelperFactory.create scmRootDir
        }

        scmHelper
    }

    private String injectVersion( String input, String version ) {
        input.replaceAll( '%version', version )
    }

}
