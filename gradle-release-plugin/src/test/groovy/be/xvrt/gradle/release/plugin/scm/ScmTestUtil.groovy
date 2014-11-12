package be.xvrt.gradle.release.plugin.scm

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.URIish

class ScmTestUtil {

    private ScmTestUtil() {
    }

    static Repository createGitRepository( File directory ) {
        Git.init().setDirectory( directory ).call();

        def repo = FileRepositoryBuilder.create( new File( directory, ".git" ) );

        def git = new Git( repo )
        git.commit().setAll( true ).setMessage( 'HEAD' ).call();

        repo
    }

    // TODO
    static Repository addOrigin( Repository repository, Repository originRepository ) {
        def config = new Config()
        config.setString( "remote", "origin", "pushurl", "short:project.git" );
        config.setString( "url", "https://server/repos/", "name", "short:" );

        RemoteConfig remoteConfig = new RemoteConfig( new Config(), "origin" );
        remoteConfig.addURI( new URIish( originRepository.getDirectory().toURI().toURL() ) )

        def git = new Git( repository )
        git.push().setRemote( originRepository.getDirectory().toURI().toString() ).call()
    }

}
