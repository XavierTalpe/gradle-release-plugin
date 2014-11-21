package be.xvrt.gradle.plugin.release.exception

import org.gradle.api.GradleException

class InvalidDependencyException extends GradleException {

    InvalidDependencyException( String message ) {
        super( message )
    }

}
