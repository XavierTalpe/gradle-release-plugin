package be.xvrt.gradle.release.plugin.scm

abstract class ScmHelper {

    abstract void commit( String message );

    abstract void tag( String name, String message );

}
