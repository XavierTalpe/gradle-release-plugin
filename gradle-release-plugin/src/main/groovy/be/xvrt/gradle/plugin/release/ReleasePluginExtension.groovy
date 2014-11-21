package be.xvrt.gradle.plugin.release

import org.gradle.api.Project

class ReleasePluginExtension {

    public static final String NAME = ReleasePlugin.RELEASE_TASK

    public static final String CHECK_DEPENDENCIES = 'checkDependencies'

    public static final String SCM_DISABLED = 'scmDisabled'

    public static final String SCM_ROOT_DIR = 'scmRootDir'
    public static final String SCM_REMOTE = 'scmRemote'
    public static final String SCM_USERNAME = 'scmUsername'
    public static final String SCM_PASSWORD = 'scmPassword'

    public static final String RELEASE_COMMIT_MSG = 'releaseCommitMessage'
    public static final String RELEASE_TAG = 'releaseTag'
    public static final String RELEASE_TAG_MSG = 'releaseTagMessage'
    public static final String UPDATE_VERSION_COMMIT_MSG = 'updateVersionCommitMessage'

    public static final String RELEASE_VERSION = 'releaseVersion'
    public static final String NEXT_VERSION = 'nextVersion'

    boolean checkDependencies

    boolean scmDisabled

    String scmRootDir
    String scmRemote
    String scmUsername
    String scmPassword

    String releaseCommitMessage
    String releaseTag
    String releaseTagMessage
    String updateVersionCommitMessage

    Closure<String> releaseVersion
    Closure<String> nextVersion

    ReleasePluginExtension( Project project ) {
        checkDependencies = true

        scmDisabled = false

        scmRootDir = project.rootDir
        scmRemote = 'origin'
        scmUsername = null
        scmPassword = null

        releaseCommitMessage = '[Gradle Release] Commit for %version.'
        releaseTag = '%version'
        releaseTagMessage = '[Gradle Release] Tag for %version.'
        updateVersionCommitMessage = '[Gradle Release] Preparing for %version.'

        releaseVersion = { version ->
            if ( version.endsWith( '-SNAPSHOT' ) ) {
                version -= '-SNAPSHOT'
            }

            version
        }

        nextVersion = { version, wasSnapshotVersion ->
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
