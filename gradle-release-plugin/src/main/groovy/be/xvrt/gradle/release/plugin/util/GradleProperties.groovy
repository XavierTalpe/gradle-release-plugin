package be.xvrt.gradle.release.plugin.util

import org.gradle.api.Project

class GradleProperties {

    private final Project project

    GradleProperties( Project project ) {
        this.project = project
    }

    def saveVersion( String version ) {
        project.version = version

        def propertiesFile = getDefaultPropertiesFile()
        if ( propertiesFile.exists() ) {
            def properties = new Properties()
            propertiesFile.withInputStream { properties.load( it ) }

            // TODO: Figure out how to get task logging tag as a prefix for this message.
            project.logger.info( ": updating gradle.properties with new version." )
            if ( properties.version ) {
                properties.put( 'version', version )
                properties.store( propertiesFile.newWriter(), null )
            }
        }
    }

    private File getDefaultPropertiesFile() {
        new File( project.projectDir, 'gradle.properties' )
    }

}
