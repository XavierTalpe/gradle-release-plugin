package be.xvrt.gradle.plugin.release.scm

import com.google.common.collect.Lists

class ScmHelperFactory {

    private static long lastScmHelperId
    private static ScmHelper lastScmHelper

    private ScmHelperFactory() {
    }

    static ScmHelper create( String scmRootDir, String username = null, String password = null ) {
        return create( new File( scmRootDir ), username, password )
    }

    static ScmHelper create( File scmRootDir, String username = null, String password = null ) {
        def scmHelperId = generateUniqueId( scmRootDir, username, password )

        if ( scmHelperId != lastScmHelperId ) {
            lastScmHelper = createNew scmRootDir, username, password
            lastScmHelperId = scmHelperId
        }

        lastScmHelper
    }

    private static ScmHelper createNew( File scmRootDir, String username = null, String password = null ) {
        def gitRepo = new File( scmRootDir, '.git' )

        def scmHelper
        if ( gitRepo.exists() ) {
            if ( hasNativeGitClient( scmRootDir ) && !username && !password ) {
                scmHelper = new NativeGitHelper( gitRepo )
            }
            else {
                scmHelper = new GitHelper( gitRepo, username, password )
            }
        }
        else {
            scmHelper = new DummyHelper()
        }

        scmHelper
    }

    private static long generateUniqueId( File scmRootDir, String username, String password ) {
        long id = scmRootDir.hashCode()

        if ( username ) {
            id += username.hashCode()
        }
        if ( password ) {
            id += password.hashCode()
        }

        id
    }

    private static boolean hasNativeGitClient( File rootDir ) {
        try {
            def process = 'git --version'.execute( Lists.newArrayList(), rootDir )
            process.waitFor()

            !process.exitValue()
        }
        catch ( IOException ignored ) {
            false
        }
    }

}
