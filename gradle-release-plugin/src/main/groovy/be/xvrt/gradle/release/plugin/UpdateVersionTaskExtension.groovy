package be.xvrt.gradle.release.plugin

class UpdateVersionTaskExtension {

    public static final String NAME = ReleasePlugin.UPDATE_VERSION_TASK
    public static final String NEXT_VERSION = 'nextVersion'

    def nextVersion;

    UpdateVersionTaskExtension() {
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
