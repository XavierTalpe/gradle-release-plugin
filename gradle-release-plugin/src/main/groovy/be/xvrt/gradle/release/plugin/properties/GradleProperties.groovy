package be.xvrt.gradle.release.plugin.properties

import org.gradle.api.Project

/**
 * Reads the gradle.properties file. If the file does not exist, we create an in-memory
 * version of it. This allows our tasks to continue working as if the gradle.properties
 * file really exists. As an additional benefit, this also ensures that no gradle.properties
 * file will be created unless it already existed. This is particularly useful for parametric builds.
 */
class GradleProperties {

    private final Project project
    private final Properties fileProperties

    protected GradleProperties( Project project ) {
        this.project = project
        this.fileProperties = new Properties()

        readFileProperties()
    }

    def String getVersion() {
        def projectVersion = project.version
        if ( projectVersion.equals( "unspecified" ) ) {
            fileProperties.get 'version'
        }
        else {
            projectVersion
        }
    }

    def setVersion( String version ) {
        project.version = version
        fileProperties.put( 'version', version )

        saveFileProperties()
    }

    private void readFileProperties() {
        def propertiesFile = getDefaultPropertiesFile()

        if ( propertiesFile.exists() ) {
            propertiesFile.withInputStream { fileProperties.load( it ) }

            def fileVersion = fileProperties.get( 'version' )
            if ( fileVersion != null ) {
                project.version = fileVersion
            }
        }
    }

    private def saveFileProperties() {
        def propertiesFile = getDefaultPropertiesFile()

        if ( propertiesFile.exists() ) {
            fileProperties.store( propertiesFile.newWriter(), null )
        }
    }

    private File getDefaultPropertiesFile() {
        new File( project.projectDir, 'gradle.properties' )
    }

}
