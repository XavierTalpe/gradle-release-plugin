package be.xvrt.gradle.plugin.release.scm

class NativeGitHelper implements ScmHelper {

    private final File projectRoot

    NativeGitHelper( File gitRepository ) {
        if ( gitRepository.name.equals( '.git' ) ) {
            projectRoot = gitRepository.parentFile
        }
        else {
            projectRoot = gitRepository
        }
    }

    @Override
    Commit commit( String message ) throws ScmException {
        def result

        try {
            result = gitExecute( ['git', 'commit', '-am', message] )
            raiseErrors result
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when committing changes.', exception )
        }

        new Commit( 'LAST' )
    }

    /**
     * Currently only supports deletion of the last commit.
     */
    @Override
    void deleteCommit( Commit commitId ) throws ScmException {
        def result = null

        try {
            if ( commitId.id.equals( 'LAST' ) ) {
                result = gitExecute( ['git', 'reset', '--hard', 'HEAD~1'] )
            }

            raiseErrors result
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when rolling back commit.', exception )
        }
    }

    @Override
    Tag tag( String name, String message ) throws ScmException {
        def result

        try {
            result = gitExecute( ['git', 'tag', '-a', name, '-m', message] )
            raiseErrors result
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when tagging changes.', exception )
        }

        new Tag( name )
    }

    @Override
    void deleteTag( Tag tag ) throws ScmException {
        def result

        try {
            result = gitExecute( ['git', 'tag', '-d', tag.id] )
            raiseErrors result
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when deleting tag.', exception )
        }
    }

    @Override
    void push( String remoteName ) throws ScmException {
        def result

        try {
            result = gitExecute( ['git', 'push', '--follow-tags', remoteName] )
            raiseErrors result
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when pushing changes.', exception )
        }
    }

    private Process gitExecute( List<String> command ) {
        def process = command.execute( ( List ) null, projectRoot )

        process.waitFor()

        process
    }

    private static void raiseErrors( Process process ) {
        if ( process.exitValue() == 0 ) {
            return
        }

        def baseOutput = process.in.text
        def errorOutput = process.err.text

        if ( baseOutput.contains( 'nothing to commit, working directory clean' ) ||
             baseOutput.contains( 'nothing added to commit but untracked files present' ) ) {
            return
        }

        def fullErrorMessage = "${errorOutput}\n\n${baseOutput}"
        throw new IllegalStateException( fullErrorMessage )
    }

}
