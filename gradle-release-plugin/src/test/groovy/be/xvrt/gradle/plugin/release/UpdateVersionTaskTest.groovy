package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.scm.ScmTestUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.gradle.api.Project
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class UpdateVersionTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Repository remoteRepository
    private Repository localRepository

    private Project project
    private PrepareReleaseTask prepareReleaseTask
    private UpdateVersionTask updateVersionTask

    @Before
    void setUp() {
        def remoteDir = temporaryFolder.newFolder()
        def localDir = temporaryFolder.newFolder()

        createPropertiesFile remoteDir, 'version', '1.0.0'
        remoteRepository = ScmTestUtil.createGitRepository remoteDir
        localRepository = ScmTestUtil.cloneGitRepository localDir, remoteRepository.directory

        project = ProjectBuilder.builder().withProjectDir( localDir ).build()
        project.apply plugin: ReleasePlugin
        project.version = '1.0.0'

        prepareReleaseTask = project.tasks.getByName( ReleasePlugin.PREPARE_RELEASE_TASK ) as PrepareReleaseTask
        updateVersionTask = project.tasks.getByName( ReleasePlugin.UPDATE_VERSION_TASK ) as UpdateVersionTask
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
    void 'next version is snapshot version'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'
        project.release {
            scmDisabled = true
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.1-SNAPSHOT', updateVersionTask.nextVersion )
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
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.1', updateVersionTask.nextVersion )
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
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        assertEquals( '1.0.0', updateVersionTask.releasedVersion )
        assertEquals( '1.0.0-SNAPSHOT-2', updateVersionTask.nextVersion )
    }

    @Test
    void 'commit is pushed when no errors occur'() {
        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( '[Gradle Release] Preparing for 1.0.1.', commitLog.get( 0 ).shortMessage )
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
        updateVersionTask.execute()

        then:
        def commitLog = new Git( localRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'override commit message'() {
        project.release {
            updateVersionCommitMessage = 'Custom prepare for %version.'
        }

        when:
        prepareReleaseTask.configure()
        prepareReleaseTask.execute()
        updateVersionTask.execute()

        then:
        def commitLog = new Git( remoteRepository ).log().call().toList()

        assertEquals( 2, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 1 ).shortMessage )
        assertEquals( 'Custom prepare for 1.0.1.', commitLog.get( 0 ).shortMessage )
    }

    @Test
    void 'commit is rolled back when push fails'() {
        setup:
        ScmTestUtil.removeOriginFrom localRepository

        when:
        try {
            prepareReleaseTask.configure()
            prepareReleaseTask.execute()
            updateVersionTask.execute()
            fail()
        }
        catch ( TaskExecutionException expected ) {
            assertTrue( expected.cause instanceof ScmException )
        }

        then:
        def commitLog = new Git( localRepository ).log().call().toList()

        assertEquals( 1, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 0 ).shortMessage )
    }

}
