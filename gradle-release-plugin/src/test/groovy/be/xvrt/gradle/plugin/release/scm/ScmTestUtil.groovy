package be.xvrt.gradle.plugin.release.scm

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

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

    static void removeOriginFrom( Repository repository ) {
        def configFile = new File( repository.directory, 'config' )
        configFile.delete()

        configFile << '[core]\n'
        configFile << '  symlinks = false\n'
        configFile << '  repositoryformatversion = 0\n'
        configFile << '  filemode = true\n'
        configFile << '  logallrefupdates = true\n'
    }

}
