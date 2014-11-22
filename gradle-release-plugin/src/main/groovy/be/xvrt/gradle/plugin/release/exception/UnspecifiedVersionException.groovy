package be.xvrt.gradle.plugin.release.exception

import org.gradle.api.GradleException

class UnspecifiedVersionException extends GradleException {

    UnspecifiedVersionException( String message ) {
        super( message )
    }

}
