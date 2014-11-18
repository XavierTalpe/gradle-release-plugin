package be.xvrt.gradle.plugin.release.scm

abstract class ScmHelper {

    abstract Commit commit( String message ) throws ScmException

    abstract void deleteCommit( Commit commit ) throws ScmException

    abstract Tag tag( String name, String message ) throws ScmException

    abstract void deleteTag( Tag tag ) throws ScmException

    abstract void push( String remoteName ) throws ScmException

}
