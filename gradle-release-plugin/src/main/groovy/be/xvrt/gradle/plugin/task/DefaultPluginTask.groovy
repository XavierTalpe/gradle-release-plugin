package be.xvrt.gradle.plugin.task
import be.xvrt.gradle.plugin.release.ReleasePlugin
import be.xvrt.gradle.plugin.release.ReleasePluginExtension
import be.xvrt.gradle.plugin.release.exception.UnspecifiedVersionException
import be.xvrt.gradle.plugin.release.util.GradleProperties
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class DefaultPluginTask extends DefaultTask {

    private ReleasePluginExtension extension

    final ReleasePluginExtension getExtension() {
        if ( !extension ) {
            extension = ( ReleasePluginExtension ) project.extensions.getByName( ReleasePlugin.EXTENSION )
        }

        extension
    }

    final String getProjectVersion() {
        def projectVersion = project.version

        if ( !projectVersion || projectVersion.equals( 'unspecified' ) ) {
            throw new UnspecifiedVersionException( "Project version ${projectVersion} is not a valid (release) version." )
        }

        projectVersion
    }

    final void setProjectVersion( String newVersion ) {
        def oldVersion = projectVersion

        project.version = newVersion

        def gradleProperties = new GradleProperties( project )
        gradleProperties.updateVersion oldVersion, newVersion, name
    }

    @SuppressWarnings( "GroovyUnusedDeclaration" )
    @TaskAction
    void runSave() {
        try {
            run()
        }
        catch ( Exception exception ) {
            rollback( exception )
            throw exception
        }
    }

    abstract void run() throws Exception

    abstract void rollback( Exception exception )

}
