package be.xvrt.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class AbstractDefaultTask extends DefaultTask {

    abstract void configure()

    @TaskAction
    void runSave() {
        try {
            run()
        }
        catch ( Exception exception ) {
            rollback( exception )
            throw exception
        }
    }

    // TODO: Replace with custom exception
    abstract void run() throws Exception

    abstract void rollback( Exception exception )

}
