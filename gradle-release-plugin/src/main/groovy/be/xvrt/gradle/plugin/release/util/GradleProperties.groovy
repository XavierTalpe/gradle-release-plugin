package be.xvrt.gradle.plugin.release.util
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

class GradleProperties {

    private final Project project

    GradleProperties( Project project ) {
        this.project = project
    }

    def updateVersion( String oldVersion, String newVersion, String taskName = '' ) {
        def propertiesFile = getDefaultPropertiesFile()

        if ( !propertiesFile.exists() ) {
            project.logger.info( ":${taskName} gradle.properties does not exist, skipping update." )
        }
        else {
            project.logger.info( ":${taskName} updating gradle.properties with new version." )

            def inputStream = new FileInputStream( propertiesFile )
            def properties = IOUtils.toString inputStream, 'UTF-8'
            inputStream.close()

            properties = ( properties =~ /${oldVersion}/ ).replaceAll newVersion

            def outputStream = new FileOutputStream( propertiesFile )
            IOUtils.write properties, outputStream, 'UTF-8'
            outputStream.close()
        }
    }

    private File getDefaultPropertiesFile() {
        new File( project.projectDir, 'gradle.properties' )
    }

}
