package be.xvrt.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class PrepareReleaseTaskTest {

    private Project project

    @Before
    void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin
    }

    @Test
    void testPrepareReleaseForSnapshotVersion() {
        project.version = '1.0.0-SNAPSHOT'
        project.tasks.prepareRelease.execute()

        assertEquals( '1.0.0', this.project.version )
    }

    @Test
    void testPrepareReleaseForNonSnapshotVersion() {
        project.version = '1.0.0-SNAPSHOT'
        project.tasks.prepareRelease.execute()

        assertEquals( '1.0.0', this.project.version )
    }

}
