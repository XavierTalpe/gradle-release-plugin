package be.xvrt.gradle.plugin.release.scm

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
