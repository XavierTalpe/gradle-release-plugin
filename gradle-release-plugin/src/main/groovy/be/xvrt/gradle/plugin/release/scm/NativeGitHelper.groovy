package be.xvrt.gradle.plugin.release.scm

import com.google.common.collect.Lists

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
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when committing changes.', exception )
        }

        if ( result && result.exitValue() != 0 ) {
            def error = result.err.text
            if ( !error.contains( 'nothing to commit, working directory clean' ) ) {
                throw new ScmException( "Error when committing changes: ${error}." )
            }
        }

        new Commit( 'LAST' )
    }

    /**
     * Currently only supports deletion of the last commit.
     */
    @Override
    void deleteCommit( Commit commitId ) throws ScmException {
        def result

        try {
            if ( commitId.id.equals( 'LAST' ) ) {
                result = gitExecute( ['git', 'reset', '--hard', 'HEAD~1'] )
            }
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when rolling back commit.', exception )
        }

        if ( result && result.exitValue() != 0 ) {
            throw new ScmException( "Error when rolling back commit: ${result.err.text}." )
        }
    }

    @Override
    Tag tag( String name, String message ) throws ScmException {
        def result

        try {
            result = gitExecute( ['git', 'tag', '-a', name, '-m', message] )
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when tagging changes.', exception )
        }

        if ( result && result.exitValue() != 0 ) {
            throw new ScmException( "Error when tagging changes: ${result.err.text}." )
        }
        else {
            new Tag( name )
        }
    }

    @Override
    void deleteTag( Tag tag ) throws ScmException {
        def result

        try {
            result = gitExecute( ['git', 'tag', '-d', tag.id] )
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when deleting tag.', exception )
        }

        if ( result && result.exitValue() != 0 ) {
            throw new ScmException( "Error when deleting tag: ${result.err.text}." )
        }
    }

    @Override
    void push( String remoteName ) throws ScmException {
        def result

        try {
            result = gitExecute( ['git', 'push', '--follow-tags', remoteName] )
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when pushing changes.', exception )
        }

        if ( result && result.exitValue() != 0 ) {
            throw new ScmException( "Error when pushing changes: ${result.err.text}." )
        }
    }

    private Process gitExecute( List<String> command ) {
        def process = command.execute( Lists.newArrayList(), projectRoot )

        process.waitFor()

        process
    }

}
