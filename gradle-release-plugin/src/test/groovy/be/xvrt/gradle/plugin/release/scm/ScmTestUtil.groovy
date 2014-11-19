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

        def git = new Git( repo )
        git.add().addFilepattern( "." ).setUpdate( false ).call()
        git.commit().setAll( true ).setMessage( 'HEAD' ).call()

        repo
    }

    static void createOrigin( Repository repository, File originDir ) {
        def originRepository = createGitRepository originDir

        def configFile = new File( repository.directory, 'config' )
        configFile << '[remote "origin"]'
        configFile << "\n\turl = file://${originRepository.directory}"
        configFile << "\n\tfetch = +refs/heads/*:refs/remotes/origin/*"
    }

}
