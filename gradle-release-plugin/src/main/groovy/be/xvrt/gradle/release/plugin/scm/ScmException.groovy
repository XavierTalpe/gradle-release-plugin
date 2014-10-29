package be.xvrt.gradle.release.plugin.scm

import org.gradle.api.GradleException

class ScmException extends GradleException {

    ScmException( String message ) {
        super( message )
    }

    ScmException( String message, Throwable cause ) {
        super( message, cause )
    }

}
