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
