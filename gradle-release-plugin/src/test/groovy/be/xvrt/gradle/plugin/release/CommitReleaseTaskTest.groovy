package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.scm.ScmTestUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class CommitReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Repository remoteRepository
    private Repository localRepository

    private Project project
    private Task prepareReleaseTask
    private Task commitReleaseTask

    @Before
    void setUp() {
        def remoteDir = temporaryFolder.newFolder()
        def localDir = temporaryFolder.newFolder()

        createPropertiesFile remoteDir, 'version', '1.0.0-SNAPSHOT'
        remoteRepository = ScmTestUtil.createGitRepository remoteDir
        localRepository = ScmTestUtil.cloneGitRepository localDir, remoteRepository.directory

        project = ProjectBuilder.builder().withProjectDir( localDir ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0-SNAPSHOT'

        prepareReleaseTask = project.tasks.getByName( ReleasePlugin.PREPARE_RELEASE_TASK ) as PrepareReleaseTask
        commitReleaseTask = project.tasks.getByName ReleasePlugin.COMMIT_RELEASE_TASK
    }

    private static void createPropertiesFile( File projectDir, String key, Object value ) {
        def propertiesFile = new File( projectDir, 'gradle.properties' )
        if ( !propertiesFile.exists() ) {
            propertiesFile.createNewFile()
        }

        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        properties.put key, value
        properties.store propertiesFile.newWriter(), null
    }

    @Test
    void 'commit is pushed when no errors occur'() {
        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( '[Gradle Release] Commit for 1.0.0.', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'executing the task will not create a commit when SCM support is disabled'() {
        setup:
        project.release {
            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( localRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

    @Test
    public void 'override commit message'() throws Exception {
        setup:
        project.release {
            releaseCommitMessage = 'Custom commit for %version.'
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        commitReleaseTask.execute()

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( 'Custom commit for 1.0.0.', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'commit is rolled back when push fails'() {
        setup:
        ScmTestUtil.removeOriginFrom localRepository

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()

        try {
            commitReleaseTask.execute()
            fail()
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

}
