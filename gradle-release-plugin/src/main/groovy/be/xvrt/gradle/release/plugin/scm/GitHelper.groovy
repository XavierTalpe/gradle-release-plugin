package be.xvrt.gradle.release.plugin.scm

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

class GitHelper extends ScmHelper {

    private final Repository repository
    private final Git git

    GitHelper( File gitRepository ) {
        repository = openRepository( gitRepository )
        git = new Git( repository )
    }

    private Repository openRepository( File gitRepository ) {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        builder.setGitDir( gitRepository )
               .readEnvironment()
               .findGitDir()
               .build();
    }

    @Override
    void commit( String message ) {
        git.commit()
           .setAll( true )
           .setMessage( message )
           .call();
    }

    @Override
    void tag( String name, String message ) {
        git.tag()
           .setName( name )
           .setMessage( message )
           .call()
    }

}
