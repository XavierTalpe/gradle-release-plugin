package be.xvrt.gradle.release.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Created by xaviert on 27.10.14.
 */
public class Test {

    public static void main(String[] args) throws IOException {

        // project.rootDir by default, test .git, .svn repo's

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        File file = new File("/home/xaviert/projects/gradle-release-plugin/.git");
        Repository repository = builder.setGitDir(file)
            .readEnvironment() // scan environment GIT_* variables
            .findGitDir() // scan up the file system tree
            .build();

        System.out.println("Having repository: " + repository.getDirectory());
        System.out.println(repository.getBranch());

        // the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
        Ref head = repository.getRef("refs/heads/master");
        System.out.println("Ref of refs/heads/master: " + head);

        StoredConfig config = repository.getConfig();
        Set<String> remote = config.getSubsections("remote");
        for (String remoteName : remote) {
            String string = config.getString("remote", remoteName, "url");
            System.out.println(string);
        }

        repository.close();
    }

}
