package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.util.GradleProperties

class PrepareReleaseTask extends AbstractDefaultTask {

    String originalVersion
    String releaseVersion

    boolean wasSnapshotVersion() {
        return originalVersion.endsWith( '-SNAPSHOT' )
    }

    @Override
    void configure() {
        prepareReleaseVersion()
    }

    @Override
    void run() {
    }

    @Override
    void rollback( Exception exception ) {
        // TODO #6 Rollback changes
        throw exception;
    }

    private void prepareReleaseVersion() {
        originalVersion = project.version
        releaseVersion = buildReleaseVersion originalVersion

        saveVersion releaseVersion
    }

    private String buildReleaseVersion( String version ) {
        def extension = project.extensions.getByName ReleasePluginExtension.NAME
        def releaseVersionClosure = extension.getAt ReleasePluginExtension.RELEASE_VERSION

        releaseVersionClosure version
    }

    private void saveVersion( String releaseVersion ) {
        logger.info "${name} setting release version to ${releaseVersion}."

        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion( releaseVersion )
    }

}
