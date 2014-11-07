package be.xvrt.gradle.release.plugin

import org.gradle.api.Project;

class ReleasePluginExtension {

    public static final String NAME = ReleasePlugin.RELEASE_TASK

    // TODO: Document and unit test.
    public static final String SCM_DISABLED = 'scmDisabled'
    public static final String SCM_ROOT_DIR = 'scmRootDir'
    public static final String SCM_REMOTE = 'scmRemote'
    public static final String SCM_USERNAME = 'scmUsername'
    public static final String SCM_PASSWORD = 'scmPassword'

    public static final String COMMIT_MSG = 'commitMessage'
    public static final String TAG_MSG = 'tagMessage'
    public static final String PREPARE_MSG = 'prepareMessage'

    public static final String RELEASE_VERSION = 'releaseVersion'
    public static final String NEXT_VERSION = 'nextVersion'

    boolean scmDisabled;
    String scmRootDir;
    String scmRemote;

    String commitMessage;
    String tagMessage;
    String prepareMessage;

    Closure<String> releaseVersion;
    Closure<String> nextVersion;

    ReleasePluginExtension( Project project ) {
        scmDisabled = false
        scmRootDir = project.rootDir
        scmRemote = 'origin'

        commitMessage = '[Gradle Release Plugin] Saving release '
        tagMessage = '[Gradle Release Plugin] Tag for '
        prepareMessage = '[Gradle Release Plugin] Preparing for '

        releaseVersion = { version ->
            if ( version.endsWith( '-SNAPSHOT' ) ) {
                version -= '-SNAPSHOT'
            }

            version
        }

        nextVersion = { version, wasSnapshotVersion ->
            // Allow user to directly specify the next version
            // from the command line using -PnextVersion=XXX.
            if ( project.hasProperty( NEXT_VERSION ) ) {
                project.property NEXT_VERSION
            }
            else {
                def lastDotIndex = version.findLastIndexOf { "." }
                def lastVersion = version.substring( lastDotIndex, version.length() )
                def incrementedVersionNumber = Integer.parseInt( lastVersion ) + 1

                def nextVersion = version.substring( 0, lastDotIndex ) + incrementedVersionNumber

                if ( wasSnapshotVersion ) {
                    nextVersion += '-SNAPSHOT'
                }

                nextVersion
            }
        }
    }

}
