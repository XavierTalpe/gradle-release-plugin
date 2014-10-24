package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class PrepareReleaseTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testConfigureWithoutPropertiesFile() throws Exception {
        Project project = ProjectBuilder.builder().build()
        project.version = '1.0.0-SNAPSHOT'
        project.apply plugin: ReleasePlugin

        assertEquals( '1.0.0-SNAPSHOT', project.version )
        project.tasks.prepareRelease.configure()
        assertEquals( '1.0.0', project.version )
    }

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
