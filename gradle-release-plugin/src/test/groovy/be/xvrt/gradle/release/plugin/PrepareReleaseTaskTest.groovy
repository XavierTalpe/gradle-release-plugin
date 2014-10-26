package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class PrepareReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Project project
    private prepareReleaseTask

    @Before
    void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin

        prepareReleaseTask = project.tasks.findByName( ReleasePlugin.PREPARE_RELEASE_TASK )
    }

    @Test
    public void testConfigureSnapshotVersion() throws Exception {
        setup:
        project.version = '1.0.0-SNAPSHOT'

        when:
        prepareReleaseTask.configure()

        then:

        assertEquals( '1.0.0-SNAPSHOT', prepareReleaseTask.originalVersion )
        assertEquals( '1.0.0', prepareReleaseTask.releaseVersion )
        assertTrue( prepareReleaseTask.wasSnapshotVersion() )
    }

    @Test
    public void testConfigureNonSnapshotVersion() throws Exception {
        setup:
        project.version = '1.0.0'

        when:
        prepareReleaseTask.configure()

        then:

        assertEquals( '1.0.0', prepareReleaseTask.originalVersion )
        assertEquals( '1.0.0', prepareReleaseTask.releaseVersion )
        assertFalse( prepareReleaseTask.wasSnapshotVersion() )
    }

    @Ignore
    @Test
    public void testConfigureWithPropertiesFile() throws Exception {
        def properties = temporaryFolder.newFile( 'gradle.properties' )
        properties.withWriter { w -> w.writeLine 'version=1.0.0-SNAPSHOT' }

        Project project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin

        assertEquals( '1.0.0-SNAPSHOT', project.version )
        project.tasks.prepareRelease.configure()
        assertEquals( '1.0.0', project.version )
    }

}
