package be.xvrt.gradle.release.plugin.scm

class DummyHelper extends ScmHelper {

    @Override
    void commit( String message ) {
    }

    @Override
    void rollbackLastCommit() {
    }

    @Override
    void tag( String name, String message ) {
    }

    @Override
    void push( String remoteName ) {
    }

}
