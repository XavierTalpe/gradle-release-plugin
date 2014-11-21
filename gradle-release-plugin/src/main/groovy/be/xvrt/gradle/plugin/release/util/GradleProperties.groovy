package be.xvrt.gradle.plugin.release.util

import org.gradle.api.Project

class GradleProperties {

    private final Project project

    GradleProperties( Project project ) {
        this.project = project
    }

    def saveVersion( String version, String taskName = '' ) {
        project.version = version

        def propertiesFile = getDefaultPropertiesFile()
        if ( propertiesFile.exists() ) {
            def properties = new Properties()
            propertiesFile.withInputStream { properties.load( it ) }

            project.logger.info( ":${taskName} updating gradle.properties with new version." )
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
