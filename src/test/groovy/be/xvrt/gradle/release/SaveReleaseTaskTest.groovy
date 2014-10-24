package be.xvrt.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class SaveReleaseTaskTest {

    private Project project

    @Before
    void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin
    }

    @Test
    void testSetNextSnapshotVersion() {
        project.version = '1.0.0-SNAPSHOT'
        project.tasks.prepareRelease.execute()
        project.tasks.saveRelease.execute()

        assertEquals( '1.0.1-SNAPSHOT', this.project.version )
    }

    @Test
    void testSetNextNonSnapshotVersion() {
        project.version = '1.0.0'
        project.tasks.prepareRelease.execute()
        project.tasks.saveRelease.execute()

        assertEquals( '1.0.1', this.project.version )
    }

}
