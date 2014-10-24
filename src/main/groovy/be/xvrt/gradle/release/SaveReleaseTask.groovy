package be.xvrt.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SaveReleaseTask extends DefaultTask {

    @TaskAction
    def setNextSnapshotVersion() {
        String snapshotVersion = project.version

        def lastDot = snapshotVersion.findLastIndexOf { '.' }
        def lastVersion = snapshotVersion.substring( lastDot, snapshotVersion.length() )
        def newVersion = Integer.parseInt( lastVersion ) + 1

        project.version = snapshotVersion.substring( 0, lastDot ) + newVersion + '-SNAPSHOT'
    }

}
