package be.xvrt.gradle.plugin.release.scm

class NativeGitHelper implements ScmHelper {

    private final File gitRepository

    NativeGitHelper( File gitRepository ) {
        this.gitRepository = gitRepository
    }

    @Override
    Commit commit( String message ) throws ScmException {
        def process

        try {
            process = "git commit -am '${message}'".execute()
            process.waitFor()
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when committing changes.', exception )
        }

        if ( process && process.exitValue() != 0 ) {
            def processOutput = process.text
            if ( !processOutput.contains( 'nothing to commit, working directory clean' ) ) {
                throw new ScmException( "Error when committing changes: ${processOutput}." )
            }
        }

        new Commit( 'LAST' )
    }

    /**
     * Currently only supports deletion of the last commit.
     */
    @Override
    void deleteCommit( Commit commitId ) throws ScmException {
        def process

        try {
            if ( commitId.id.equals( 'LAST' ) ) {
                process = 'git reset --hard HEAD~1'.execute()
                process.waitFor()
            }
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when rolling back commit.', exception )
        }

        if ( process && process.exitValue() != 0 ) {
            throw new ScmException( "Error when rolling back commit: ${process.text}." )
        }
    }

    @Override
    Tag tag( String name, String message ) throws ScmException {
        def process

        try {
            process = "git tag -a ${name} -m '${message}'".execute()
            process.waitFor()
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when tagging changes.', exception )
        }

        if ( process && process.exitValue() != 0 ) {
            throw new ScmException( "Error when tagging changes: ${process.text}." )
        }
        else {
            new Tag( name )
        }
    }

    @Override
    void deleteTag( Tag tag ) throws ScmException {
        def process

        try {
            process = "git tag -d ${tag.id}".execute()
            process.waitFor()
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when deleting tag.', exception )
        }

        if ( process && process.exitValue() != 0 ) {
            throw new ScmException( "Error when deleting tag: ${process.text}." )
        }
    }

    @Override
    void push( String remoteName ) throws ScmException {
        def process

        try {
            process = "git push ${remoteName}".execute()
            process.waitFor()
        }
        catch ( Exception exception ) {
            throw new ScmException( 'Error when pushing changes.', exception )
        }

        if ( process && process.exitValue() != 0 ) {
            throw new ScmException( "Error when pushing changes: ${process.text}." )
        }
    }

}
