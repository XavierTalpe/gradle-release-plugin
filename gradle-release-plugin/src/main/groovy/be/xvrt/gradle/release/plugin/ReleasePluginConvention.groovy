package be.xvrt.gradle.release.plugin

import org.gradle.api.Project;

class ReleasePluginConvention {

    public static final String SCM_ROOT_DIR = 'scmRootDir'

    String scmRootDir;

    ReleasePluginConvention( Project project ) {
        scmRootDir = project.rootDir // TODO Can it still be overwritten now?
    }

}
