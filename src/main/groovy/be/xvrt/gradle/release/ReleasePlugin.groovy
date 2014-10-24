package be.xvrt.gradle.release

import org.gradle.api.Plugin
import org.gradle.api.Project

class ReleasePlugin implements Plugin<Project> {

    void apply( Project target ) {
        target.task( 'prepareRelease', type: PrepareReleaseTask )
        target.task( 'release', type: ReleaseTask )
        target.task( 'saveRelease', type: SaveReleaseTask )
    }

}
