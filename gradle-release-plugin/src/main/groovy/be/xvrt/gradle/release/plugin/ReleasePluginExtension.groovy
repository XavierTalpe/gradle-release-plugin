package be.xvrt.gradle.release.plugin

import org.gradle.api.Project;

class ReleasePluginExtension {

    public static final String SCM_ROOT_DIR = 'scmRootDir'
    public static final String SCM_USERNAME = 'scmUsername'
    public static final String SCM_PASSWORD = 'scmPassword'

    String scmRootDir;

    ReleasePluginExtension( Project project ) {
        scmRootDir = project.rootDir
    }

}
