package be.xvrt.gradle.plugin.release.scm
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

class GitHelper extends ScmHelper {

    private final Repository repository
    private final Git git

    GitHelper( File gitRepository ) {
        repository = openRepository gitRepository
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
    Commit commit( String message ) throws ScmException {
        try {
            def commit = git.commit()
                            .setAll( true )
                            .setMessage( message )
                            .call()

            new Commit( commit.id.name )
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when committing changes.', exception )
        }
    }

    /**
     * Currently only supports deletion of the last commit.
     */
    @Override
    void deleteCommit( Commit commitId ) throws ScmException {
        try {
            def commitLog = git.log().call().toList()
            def lastCommit = commitLog.first()

            def lastCommitName = lastCommit.id.name
            def targetName = commitId.id

            if ( lastCommitName.equals( targetName ) ) {
                git.reset().setMode( ResetCommand.ResetType.HARD ).setRef( 'HEAD~1' ).call()
            }
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when rolling back commit.', exception )
        }
    }

    @Override
    Tag tag( String name, String message ) throws ScmException {
        try {
            git.tag()
               .setName( name )
               .setMessage( message )
               .call()

            new Tag( name )
        } catch ( Exception exception ) {
            throw new ScmException( 'Error when tagging changes.', exception )
        }
    }

    @Override
    void deleteTag( Tag tag ) throws ScmException {
        try {
            git.tagDelete().setTags( tag.id ).call()
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when rolling back commit.', exception )
        }
    }

    @Override
    void push( String remoteName ) throws ScmException {
        def remoteUri = findRemoteUri remoteName
        if ( remoteUri == null ) {
            throw new ScmException( 'Error when pushing changes. No remote defined.' )
        }

        try {
            git.push().setRemote( remoteUri ).call()
        } catch ( Exception exception ) {
            throw new ScmException( 'Error when pushing changes.', exception )
        }
    }

    private String findRemoteUri( String targetRemoteName ) {
        def config = repository.getConfig()
        def allRemotes = config.getSubsections( 'remote' )

        def remoteUri = null
        if ( allRemotes.size() == 1 ) {
            def remoteName = allRemotes.iterator().next()
            remoteUri = config.getString( 'remote', remoteName, 'url' )
        }
        else {
            for ( def remoteName : allRemotes ) {
                if ( remoteName.equals( targetRemoteName ) ) {
                    remoteUri = config.getString( 'remote', remoteName, 'url' )
                    break;
                }
            }
        }

        remoteUri
    }

}
