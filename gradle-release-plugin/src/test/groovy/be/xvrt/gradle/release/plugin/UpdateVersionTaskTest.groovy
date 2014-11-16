package be.xvrt.gradle.release.plugin

import be.xvrt.gradle.release.plugin.scm.ScmTestUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class UpdateVersionTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Repository gradleRepository

    private Project project
    private Task prepareReleaseTask
    private Task updateVersionTask

    @Before
    void setUp() {
        def repoFolder = temporaryFolder.newFolder()
        gradleRepository = ScmTestUtil.createGitRepository repoFolder

        project = ProjectBuilder.builder().withProjectDir( repoFolder ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        prepareReleaseTask = project.tasks.getByName ReleasePlugin.PREPARE_RELEASE_TASK
        updateVersionTask = project.tasks.getByName ReleasePlugin.UPDATE_VERSION_TASK
    }

    @Test
    void 'next version is snapshot version'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'
        project.release {
            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        updateVersionTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.1-SNAPSHOT', updateVersionTask.nextVersion )
        assertEquals( '1.0.1-SNAPSHOT', project.version )
    }

    @Test
    void 'next version is non-snapshot version'() {
        setup:
        project.version = '1.0.0'
        project.release {
            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        updateVersionTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.1', updateVersionTask.nextVersion )
        assertEquals( '1.0.1', project.version )
    }

    @Test
    void 'next version is defined by custom closure'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'
        project.release {
            nextVersion = { version, wasSnapshotVersion ->
                if ( wasSnapshotVersion ) {
                    version = version + '-SNAPSHOT'
                }

                version + '-2'
            }

            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        updateVersionTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.0-SNAPSHOT-2', updateVersionTask.nextVersion )
        assertEquals( '1.0.0-SNAPSHOT-2', project.version )
    }

    @Test
    void 'commit is pushed when no errors occur'() {
        setup:
        ScmTestUtil.createOrigin gradleRepository, temporaryFolder.newFolder()

        when:
        prepareReleaseTask.configure()
        updateVersionTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( gradleRepository ).log().call()

        def nbCommits = 0;
        for ( RevCommit commit : commitLog ) {
            if ( !commit.getShortMessage().equals( 'HEAD' ) ) {
                assertEquals( '[Gradle Release] Preparing for 1.0.1.', commit.getShortMessage() )
                nbCommits++;
            }
        }

        assertEquals( 1, nbCommits )
    }

    @Ignore
    @Test
    void 'gradle.properties file is updated when no errors occur'() {
    }

    @Test
    void 'executing the task won\'t create a commit when SCM support is disabled'() {
        setup:
        project.release {
            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        updateVersionTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( gradleRepository ).log().call()
        def nbNewCommits = commitLog.count { commit ->
            !commit.getShortMessage().equals( 'HEAD' )
        }

        assertEquals( 0, nbNewCommits )
    }

    @Test
    public void 'override commit message'() throws Exception {
        setup:
        ScmTestUtil.createOrigin gradleRepository, temporaryFolder.newFolder()

        project.release {
            updateVersionCommitMessage = 'Custom prepare for %version.'
        }

        when:
        prepareReleaseTask.configure()
        updateVersionTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( gradleRepository ).log().call()

        def nbCommits = 0;
        for ( RevCommit commit : commitLog ) {
            if ( !commit.getShortMessage().equals( 'HEAD' ) ) {
                assertEquals( 'Custom prepare for 1.0.1.', commit.getShortMessage() )
                nbCommits++;
            }
        }

        assertEquals( 1, nbCommits )
    }

    // TODO #6 Rollback changes in gradle file
    @Ignore
    @Test
    void 'commit is rolled back when push fails'() {
        setup:
        ScmTestUtil.createOrigin gradleRepository, temporaryFolder.newFolder()

        when:
        prepareReleaseTask.configure()
        updateVersionTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( gradleRepository ).log().call()
        def nbNewCommits = commitLog.count { commit ->
            !commit.getShortMessage().equals( 'HEAD' )
        }

        assertEquals( 0, nbNewCommits )
    }

}
