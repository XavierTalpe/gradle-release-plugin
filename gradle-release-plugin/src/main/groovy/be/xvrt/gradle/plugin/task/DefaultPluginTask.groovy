package be.xvrt.gradle.plugin.task

import be.xvrt.gradle.plugin.release.ReleasePlugin
import be.xvrt.gradle.plugin.release.ReleasePluginExtension
import be.xvrt.gradle.plugin.release.exception.UnspecifiedVersionException
import be.xvrt.gradle.plugin.release.util.GradleProperties
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class DefaultPluginTask extends DefaultTask {

    protected final ReleasePluginExtension extension

    protected final GradleProperties gradleProperties

    DefaultPluginTask() {
        extension = ( ReleasePluginExtension ) project.extensions.getByName( ReleasePlugin.EXTENSION )

        gradleProperties = new GradleProperties( project )
    }

    protected final String getProjectVersion() {
        def projectVersion

        if ( project.plugins.hasPlugin( 'android' ) || project.plugins.hasPlugin( 'android-library' ) ) {
            // TODO: Verify
            projectVersion = project.android?.defaultConfig?.versionName
        }
        else {
            projectVersion = project.version
        }

        if ( !projectVersion || projectVersion.equals( 'unspecified' ) ) {
            throw new UnspecifiedVersionException( "Project version ${projectVersion} is not a valid (release) version." )
        }

        projectVersion
    }

    protected final void setProjectVersion( String newVersion ) {
        def oldVersion = projectVersion

        project.version = newVersion

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

    protected abstract void run() throws Exception

    protected abstract void rollback( Exception exception )

}
