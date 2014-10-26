package be.xvrt.gradle.release.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class TagReleaseTask extends DefaultTask {

    @TaskAction
    def setNextReleaseVersion() {
//        String nextProjectVersion = buildNextReleaseVersion()
//        gradleProperties.setVersion( nextProjectVersion )
//        logger.debug( "Project version set to ${nextProjectVersion}." )
    }

    private String buildNextReleaseVersion() {
        String projectVersion = project.version

        def lastDotIndex = projectVersion.findLastIndexOf { "." }
        def lastVersion = projectVersion.substring( lastDotIndex, projectVersion.length() )
        def newVersion = Integer.parseInt( lastVersion ) + 1

        def nextProjectVersion = projectVersion.substring( 0, lastDotIndex ) + newVersion

        if ( project.tasks.prepareRelease.wasSnapshotVersion ) {
            nextProjectVersion += '-SNAPSHOT'
        }

        nextProjectVersion
    }


}
