package be.xvrt.gradle.release.plugin.scm

class ScmHelperFactory {

    private ScmHelperFactory() {
    }

    static ScmHelper create( String scmRootDir ) {
        return create( new File( scmRootDir ) )
    }

    static ScmHelper create( File scmRootDir ) {
        def gitRepo = new File( scmRootDir, '.git' )
        if ( gitRepo.exists() ) {
            return new GitHelper( gitRepo )
        }
        else {
            return new DummyHelper()
        }
    }

}
