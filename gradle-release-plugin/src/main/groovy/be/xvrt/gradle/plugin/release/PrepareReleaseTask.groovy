package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.exception.InvalidDependencyException
import be.xvrt.gradle.plugin.release.util.GradleProperties
import be.xvrt.gradle.plugin.task.AbstractDefaultTask

class PrepareReleaseTask extends AbstractDefaultTask {

    String originalVersion
    String releaseVersion

    boolean wasSnapshotVersion() {
        return originalVersion.contains( 'SNAPSHOT' )
    }

    @Override
    void configure() {
        prepareReleaseVersion()
    }

    @Override
    void run() {
        def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK
        def allowSnapshotDependencies = extension.getAt( ReleasePluginExtension.ALLOW_SNAPSHOT_DEPENDENCIES )

        if ( !allowSnapshotDependencies ) {
            checkSnapshotDependencies()
        }
    }

    @Override
    void rollback( Exception exception ) {
        rollbackVersion originalVersion
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
        logger.info ":${name} setting release version to ${releaseVersion}."

        def gradleProperties = new GradleProperties( project )
        gradleProperties.saveVersion( releaseVersion )
    }

    private void rollbackVersion( String version ) {
        if ( version ) {
            logger.info ":${name} rolling back version to ${version}."

            def gradleProperties = new GradleProperties( project )
            gradleProperties.saveVersion( version )
        }
    }

    private void checkSnapshotDependencies() {
        def snapshotDependencies = new HashSet<String>()

        project.configurations.each { config ->
            config.dependencies?.each { dep ->
                if ( dep.version?.contains( 'SNAPSHOT' ) ) {
                    snapshotDependencies.add "${project.name} - ${dep.group}:${dep.name}:${dep.version}"
                }
            }
        }

        if ( snapshotDependencies.size() > 0 ) {
            def snapshotsList = snapshotDependencies.join( '\n' )
            def errorMessage = "Cannot release project with SNAPSHOT dependencies:\n${snapshotsList}\n"

            throw new InvalidDependencyException( errorMessage )
        }
    }

}