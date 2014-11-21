package be.xvrt.gradle.plugin.release.scm

interface ScmHelper {

    Commit commit( String message ) throws ScmException

    void deleteCommit( Commit commit ) throws ScmException

    Tag tag( String name, String message ) throws ScmException

    void deleteTag( Tag tag ) throws ScmException

    void push( String remoteName ) throws ScmException

}
