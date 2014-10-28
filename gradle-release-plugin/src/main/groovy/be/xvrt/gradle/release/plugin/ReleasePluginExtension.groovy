package be.xvrt.gradle.release.plugin

import org.gradle.api.Project;

class ReleasePluginExtension {

    public static final String SCM_ROOT_DIR = 'scmRootDir'
    public static final String SCM_USERNAME = 'scmUsername'
    public static final String SCM_PASSWORD = 'scmPassword'

    public static final String COMMIT_MSG = 'commitMessage'
    public static final String TAG_MSG = 'tagMessage'

    String scmRootDir;

    String commitMessage;
    String tagMessage;

    ReleasePluginExtension( Project project ) {
        scmRootDir = project.rootDir

        commitMessage = '[Gradle Release Plugin] Saving release '
        tagMessage = '[Gradle Release Plugin] Tag for '
    }

}
