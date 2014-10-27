package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class ReleasePluginConventionTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
    }

    @Test
    void testDefaultScmRootDir() {
        given:
        def releaseConvention = new ReleasePluginConvention( project )

        then:
        assertEquals( temporaryFolder.root.toString(), releaseConvention.scmRootDir )
    }

}
