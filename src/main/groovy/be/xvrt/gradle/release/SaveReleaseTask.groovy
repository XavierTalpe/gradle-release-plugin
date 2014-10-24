package be.xvrt.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SaveReleaseTask extends DefaultTask {

    @TaskAction
    def setNextReleaseVersion() {
        String projectVersion = project.version

        def lastDotIndex = projectVersion.findLastIndexOf { '.' }
        def lastVersion = projectVersion.substring( lastDotIndex, projectVersion.length() )
        def newVersion = Integer.parseInt( lastVersion ) + 1

        def nextProjectVersion = projectVersion.substring( 0, lastDotIndex ) + newVersion

        if ( project.prepareRelease.isSnapshotVersion ) {
            nextProjectVersion += '-SNAPSHOT'
        }

        project.version = nextProjectVersion
    }


}
