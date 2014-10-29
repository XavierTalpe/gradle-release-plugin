package be.xvrt.gradle.release.plugin

import org.gradle.api.Project;

class ReleasePluginExtension {

    public static final String SCM_ROOT_DIR = 'scmRootDir'
    public static final String SCM_REMOTE = 'scmRemote'
    public static final String SCM_USERNAME = 'scmUsername'
    public static final String SCM_PASSWORD = 'scmPassword'

    public static final String COMMIT_MSG = 'commitMessage'
    public static final String TAG_MSG = 'tagMessage'
    public static final String PREPARE_MSG = 'prepareMessage'

    String scmRootDir;
    String scmRemote;

    String commitMessage;
    String tagMessage;
    String prepareMessage;

    ReleasePluginExtension( Project project ) {
        scmRootDir = project.rootDir
        scmRemote = 'origin'

        commitMessage = '[Gradle Release Plugin] Saving release '
        tagMessage = '[Gradle Release Plugin] Tag for '
        prepareMessage = '[Gradle Release Plugin] Preparing for '
    }

}
