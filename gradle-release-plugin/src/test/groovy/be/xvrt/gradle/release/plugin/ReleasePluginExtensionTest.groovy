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
    void setUp() {
        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin
    }

    @Test
    void testDefaultScmRootDir() {
        assertEquals( temporaryFolder.root.toString(), project.release.scmRootDir )
    }

    @Test
    void testOverwriteValues() throws Exception {
        when:
        project.release {
            scmRootDir = '~/home/xaviert'
            scmRemote = 'origin2'

            commitMessage = 'Commit'
            tagMessage = 'Tag'
        }

        then:
        assertEquals( '~/home/xaviert', project.release.scmRootDir )
        assertEquals( 'origin2', project.release.scmRemote )

        assertEquals( 'Commit', project.release.commitMessage )
        assertEquals( 'Tag', project.release.tagMessage )
    }

}
