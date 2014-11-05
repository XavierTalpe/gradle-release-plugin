package be.xvrt.gradle.release.plugin.scm

class ScmHelperFactory {

    private final static Map<File, ScmHelper> CACHE = new HashMap<>( 1 )

    private ScmHelperFactory() {
    }

    static ScmHelper create( String scmRootDir ) {
        return create( new File( scmRootDir ) )
    }

    static ScmHelper create( File scmRootDir ) {
        def scmHelper = CACHE.get( scmRootDir )

        if ( scmHelper == null ) {
            scmHelper = createNew( scmRootDir, scmHelper )
            CACHE.put scmRootDir, scmHelper
        }

        scmHelper
    }

    private static ScmHelper createNew( File scmRootDir, ScmHelper scmHelper ) {
        def gitRepo = new File( scmRootDir, '.git' )

        if ( gitRepo.exists() ) {
            scmHelper = new GitHelper( gitRepo )
        }
        else {
            scmHelper = new DummyHelper()
        }

        scmHelper
    }

}
