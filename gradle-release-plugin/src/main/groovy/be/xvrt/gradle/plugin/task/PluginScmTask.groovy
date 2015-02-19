package be.xvrt.gradle.plugin.task

import be.xvrt.gradle.plugin.release.ReleasePluginExtension
import be.xvrt.gradle.plugin.release.scm.*

abstract class PluginScmTask extends DefaultPluginTask {

    private scmHelper

    protected final boolean isScmSupportDisabled() {
        extension.getAt ReleasePluginExtension.SCM_DISABLED
    }

    protected final Commit commit( String commitMessage, String version ) throws ScmException {
        logger.info ":${project.name}:${name} committing local changes."

        commitMessage = injectVersion commitMessage, version

        getScmHelper().commit commitMessage
    }

    protected final Tag tag( String tagName, String tagMessage, String version ) throws ScmException {
        logger.info ":${project.name}:${name} tagging release."

        tagName = injectVersion tagName, version
        tagMessage = injectVersion tagMessage, version

        getScmHelper().tag tagName, tagMessage
    }

    protected final void push() throws ScmException {
        def scmRemote = extension.getAt( ReleasePluginExtension.SCM_REMOTE ) as String

        logger.info ":${project.name}:${name} pushing local changes to ${scmRemote}."

        getScmHelper().push scmRemote
    }

    protected final void pushTag( Tag tag ) throws ScmException {
        def scmRemote = extension.getAt( ReleasePluginExtension.SCM_REMOTE ) as String

        logger.info ":${project.name}:${name} pushing local tag to ${scmRemote}."

        getScmHelper().pushTag scmRemote, tag
    }

    protected final ScmHelper getScmHelper() {
        if ( !scmHelper ) {
            def scmRootDir = extension.getAt( ReleasePluginExtension.SCM_ROOT_DIR ) as String
            def scmUsername
            def scmPassword

            // Allow user to directly specify username and password from the
            // command line using -PscmUsername=XXX -PscmPassword=XXX.
            // This takes precedence over the properties extension.
            if ( project.hasProperty( ReleasePluginExtension.SCM_USERNAME ) &&
                 project.hasProperty( ReleasePluginExtension.SCM_PASSWORD ) ) {
                scmUsername = project.property( ReleasePluginExtension.SCM_USERNAME ) as String
                scmPassword = project.property( ReleasePluginExtension.SCM_PASSWORD ) as String
            }
            else {
                scmUsername = extension.getAt( ReleasePluginExtension.SCM_USERNAME ) as String
                scmPassword = extension.getAt( ReleasePluginExtension.SCM_PASSWORD ) as String
            }

            scmHelper = ScmHelperFactory.create scmRootDir, scmUsername, scmPassword
        }

        scmHelper
    }

    private static String injectVersion( String input, String version ) {
        input.replaceAll( '%version', version )
    }

}
