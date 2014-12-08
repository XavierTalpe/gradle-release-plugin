package be.xvrt.gradle.plugin.release.scm

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

// TODO: Investigate if not closing of repositories introduces leaks.
class ScmTestUtil {

    private ScmTestUtil() {
    }

    static Repository createGitRepository( File directory ) {
        Git.init().setDirectory( directory ).call()

        def repo = FileRepositoryBuilder.create( new File( directory, ".git" ) )

        def git
        try {
            git = new Git( repo )
            git.add().addFilepattern( "." ).setUpdate( false ).call()
            git.commit().setAll( true ).setMessage( 'HEAD' ).call()
        }
        finally {
            if ( git ) {
                git.close()
            }
        }

        git.repository
    }

    static Repository cloneGitRepository( File checkoutDir, File remoteRepository ) {
        def remoteUri = remoteRepository.toURI()

        def git
        try {
            git = Git.cloneRepository()
                     .setURI( remoteUri.toString() )
                     .setDirectory( checkoutDir )
                     .call();
        } finally {
            if ( git ) {
                git.close();
            }
        }

        git.repository
    }

    @Deprecated
    static Repository createOrigin( Repository repository, File originDir ) {
        def originRepository = createGitRepository originDir

        def configFile = new File( repository.directory, 'config' )
        configFile << '[remote "origin"]'
        configFile << "\n\turl = file://${originRepository.directory}"
        configFile << "\n\tfetch = +refs/heads/*:refs/remotes/origin/*"

        originRepository
    }

}
