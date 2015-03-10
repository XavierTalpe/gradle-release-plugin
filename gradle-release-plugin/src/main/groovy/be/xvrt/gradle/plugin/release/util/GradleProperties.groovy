package be.xvrt.gradle.plugin.release.util

import org.apache.commons.io.IOUtils
import org.gradle.api.Project

class GradleProperties {

    private final Project project

    GradleProperties( Project project ) {
        this.project = project
    }

    def updateVersion( String newVersion, String taskName = '' ) {
        updateFile( 'build.gradle', newVersion, taskName )
        updateFile( 'gradle.properties', newVersion, taskName )
    }

    private updateFile( String filename, String newVersion, String taskName ) {
        def file = new File( project.projectDir, filename )

        if ( !file.exists() ) {
            project.logger.info( ":${project.name}:${taskName} ${filename} does not exist, skipping update." )
        }
        else {
            project.logger.info( ":${project.name}:${taskName} updating ${filename} with new version." )
            writeVersion( file, newVersion )
        }
    }

    private static writeVersion( File file, String newVersion ) {
        def inputStream = new FileInputStream( file )
        def properties = IOUtils.toString inputStream, 'UTF-8'
        inputStream.close()

        properties = ( properties =~ /version\s*=\s*[\w-.]+/ ).replaceAll "version=${newVersion}"

        def outputStream = new FileOutputStream( file )
        IOUtils.write properties, outputStream, 'UTF-8'
        outputStream.close()
    }

}
