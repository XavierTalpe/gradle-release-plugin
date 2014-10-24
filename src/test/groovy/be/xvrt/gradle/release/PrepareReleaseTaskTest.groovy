package be.xvrt.gradle.release
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class PrepareReleaseTaskTest {

    private Project project

    @Before
    public void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin

        project.version = '1.0.0-SNAPSHOT'
    }

    @Test
    public void getProjectVersion() {
        assertEquals( '1.0.0-SNAPSHOT', this.project.version )
        project.prepareRelease.execute()
        assertEquals( '1.0.0', this.project.version )
    }
}
