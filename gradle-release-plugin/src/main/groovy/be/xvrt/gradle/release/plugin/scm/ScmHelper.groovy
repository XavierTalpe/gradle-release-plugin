package be.xvrt.gradle.release.plugin.scm

abstract class ScmHelper {

    abstract void commit( String message ) throws ScmException

    abstract void rollbackLastCommit() throws ScmException

    abstract void tag( String name, String message ) throws ScmException

    abstract void push( String remoteName ) throws ScmException

}
