package be.xvrt.gradle.release.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class ReleasePluginExtensionTest {

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
        assertEquals( temporaryFolder.root.toString(), project.release.scmRootDir )
    }

    @Test
    public void testOverwriteScmRootDir() throws Exception {
        when:
        project.release {
            scmRootDir = '~/home/xaviert'
        }

        then:
        assertEquals( '~/home/xaviert', project.release.scmRootDir )
    }

}
