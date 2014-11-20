package be.xvrt.gradle.plugin.release.scm

class DummyHelper implements ScmHelper {

    @Override
    Commit commit( String message ) {
        new Commit( '' )
    }

    @Override
    void deleteCommit( Commit commit ) {
    }

    @Override
    Tag tag( String name, String message ) {
        new Tag( '' )
    }

    @Override
    void deleteTag( Tag tag ) {
    }

    @Override
    void push( String remoteName ) {
    }

}
