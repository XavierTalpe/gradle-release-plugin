package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmHelper
import be.xvrt.gradle.release.plugin.scm.ScmHelperFactory

// TODO: Write test?
abstract class AbstractScmTask extends AbstractDefaultTask {

    protected AbstractScmTask() {
    }

    protected final boolean isScmSupportDisabled() {
        def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK
        extension.getAt ReleasePluginExtension.SCM_DISABLED
    }

    protected final ScmHelper getScmHelper() {
        def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK
        def scmRootDir = extension.getAt ReleasePluginExtension.SCM_ROOT_DIR

        ScmHelperFactory.create scmRootDir
    }

    protected final String injectVersion( String input, String version ) {
        input.replaceAll( '%version', version )
    }

}
