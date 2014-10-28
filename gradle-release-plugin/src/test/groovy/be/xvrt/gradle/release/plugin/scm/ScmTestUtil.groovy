package be.xvrt.gradle.release.plugin.scm

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

class ScmTestUtil {

    private ScmTestUtil() {
    }

    static Repository createGitRepository( File directory ) {
        Git.init().setDirectory( directory ).call();

        FileRepositoryBuilder.create( new File( directory, ".git" ) );
    }

}
