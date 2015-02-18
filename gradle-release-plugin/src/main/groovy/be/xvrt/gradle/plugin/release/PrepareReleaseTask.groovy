package be.xvrt.gradle.plugin.release
import be.xvrt.gradle.plugin.release.exception.InvalidDependencyException
import be.xvrt.gradle.plugin.task.DefaultPluginTask

class PrepareReleaseTask extends DefaultPluginTask {

    String originalVersion
    String releaseVersion

    boolean wasSnapshotVersion() {
        return originalVersion.contains( 'SNAPSHOT' )
    }

    void configure() {
        originalVersion = projectVersion
        releaseVersion = prepareReleaseVersion originalVersion
    }

    @Override
    void run() {
        def extension = project.extensions.getByName ReleasePluginExtension.NAME
        def allowSnapshotDependencies = extension.getAt( ReleasePluginExtension.ALLOW_SNAPSHOT_DEPENDENCIES )

        if ( !allowSnapshotDependencies ) {
            checkSnapshotDependencies()
        }
    }

    @Override
    void rollback( Exception exception ) {
        rollbackVersion originalVersion
    }

    private String prepareReleaseVersion( String originalVersion ) {
        def releaseVersion = buildReleaseVersion originalVersion
        saveVersion releaseVersion

        releaseVersion
    }

    private String buildReleaseVersion( String version ) {
        def releaseVersionClosure = extension.getAt( ReleasePluginExtension.RELEASE_VERSION ) as Closure<String>

        releaseVersionClosure version
    }

    private void saveVersion( String releaseVersion ) {
        logger.info ":${project.name}:${name} setting release version to ${releaseVersion}."

        projectVersion = releaseVersion
    }

    private void rollbackVersion( String version ) {
        if ( version ) {
            logger.info ":${project.name}:${name} rolling back uncommitted version to ${version}."

            projectVersion = version
        }
    }

    private void checkSnapshotDependencies() {
        def snapshotDependencies = new HashSet<String>()

        project.configurations.each() { config ->
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
