package be.xvrt.gradle.plugin.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class ReleasePluginExtensionTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
    }

    @Test
    void testDefaultValues() {
        then:
        assertFalse( project.release.scmDisabled )

        assertEquals( temporaryFolder.root.toString(), project.release.scmRootDir )
        assertEquals( 'origin', project.release.scmRemote )

        assertEquals( '[Gradle Release] Commit for %version.', project.release.releaseCommitMessage )
        assertEquals( '%version', project.release.releaseTag )
        assertEquals( '[Gradle Release] Tag for %version.', project.release.releaseTagMessage )
        assertEquals( '[Gradle Release] Preparing for %version.', project.release.updateVersionCommitMessage )
    }

    @Test
    void testOverwriteValues() {
        when:
        project.release {
            scmDisabled = true

            scmRootDir = '~/home/xaviert'
            scmRemote = 'origin2'

            releaseCommitMessage = 'releaseCommitMessage'
            releaseTag = 'releaseTag'
            releaseTagMessage = 'releaseTagMessage'
            updateVersionCommitMessage = 'updateVersionCommitMessage'
        }

        then:
        assertTrue project.release.scmDisabled

        assertEquals( '~/home/xaviert', project.release.scmRootDir )
        assertEquals( 'origin2', project.release.scmRemote )

        assertEquals( 'releaseCommitMessage', project.release.releaseCommitMessage )
        assertEquals( 'releaseTag', project.release.releaseTag )
        assertEquals( 'releaseTagMessage', project.release.releaseTagMessage )
        assertEquals( 'updateVersionCommitMessage', project.release.updateVersionCommitMessage )
    }

}
