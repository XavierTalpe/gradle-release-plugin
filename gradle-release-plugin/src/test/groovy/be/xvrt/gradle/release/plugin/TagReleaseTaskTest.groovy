package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertEquals

class TagReleaseTaskTest {

    private Project project

    @Before
    void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.apply plugin: ReleasePlugin
    }

    @Ignore
    @Test
    void testSetNextSnapshotVersion() {
        project.version = '1.0.0-SNAPSHOT'
        project.tasks.prepareRelease.execute()
        project.tasks.tagRelease.execute()

        assertEquals( '1.0.1-SNAPSHOT', this.project.version )
    }

    @Ignore
    @Test
    void testSetNextNonSnapshotVersion() {
        project.version = '1.0.0'
        project.tasks.prepareRelease.execute()
        project.tasks.tagRelease.execute()

        assertEquals( '1.0.1', this.project.version )
    }

}
