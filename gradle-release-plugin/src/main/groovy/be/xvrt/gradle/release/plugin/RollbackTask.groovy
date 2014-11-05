package be.xvrt.gradle.release.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class RollbackTask extends DefaultTask {

    abstract void configure()

    @TaskAction
    void runSave() {
        try {
            run()
        }
        catch ( Exception exception ) {
            exception.printStackTrace()
            rollback( exception )
        }
    }

    // TODO: Replace with custom exception
    abstract void run() throws Exception

    abstract void rollback( Exception exception )

}
