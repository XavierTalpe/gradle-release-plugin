package be.xvrt.gradle.release.plugin.properties

import be.xvrt.gradle.release.plugin.ReleasePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class GradlePropertiesFileTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File propertiesFile

    private GradleProperties gradleProperties

    @Before
    public void setUp() throws Exception {
        propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile.withWriter { w -> w.writeLine 'version=1.0.0-SNAPSHOT' }

        Project project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin

        gradleProperties = new GradleProperties( project )
    }

    @Test
    void testGetVersion() {
        assertEquals( '1.0.0-SNAPSHOT', gradleProperties.getVersion() )
    }

    @Test
    public void testSetVersion() throws Exception {
        gradleProperties.setVersion( '1.0.0' )

        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.0', properties.version )
    }

}
