package be.xvrt.gradle.release.plugin

import org.gradle.api.DefaultTask

abstract class RollbackTask extends DefaultTask {

    abstract def configure()

    abstract def rollback()

}
