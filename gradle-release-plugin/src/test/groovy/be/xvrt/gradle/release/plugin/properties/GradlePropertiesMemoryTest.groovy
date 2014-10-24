package be.xvrt.gradle.release.plugin.properties

import be.xvrt.gradle.release.plugin.ReleasePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class GradlePropertiesMemoryTest {

    private GradleProperties gradleProperties

    @Before
    public void setUp() throws Exception {
        Project project = ProjectBuilder.builder().build()
        project.version = '1.0.0-SNAPSHOT'
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

        assertEquals( '1.0.0', gradleProperties.getVersion() )
    }


}
