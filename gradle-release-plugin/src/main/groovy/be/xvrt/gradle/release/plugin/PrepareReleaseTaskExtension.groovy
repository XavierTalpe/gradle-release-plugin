package be.xvrt.gradle.release.plugin

class PrepareReleaseTaskExtension {

    public static final String RELEASE_VERSION = 'releaseVersion'

    def releaseVersion;

    PrepareReleaseTaskExtension() {
        releaseVersion = { version ->
            if ( version.endsWith( '-SNAPSHOT' ) ) {
                version -= '-SNAPSHOT'
            }

            version
        }
    }

}
