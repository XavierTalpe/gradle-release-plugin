package be.xvrt.gradle.plugin.task

import be.xvrt.gradle.plugin.release.ReleasePlugin
import be.xvrt.gradle.plugin.release.exception.UnspecifiedVersionException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class DefaultPluginTaskTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Project project
    private DefaultPluginTask defaultPluginTask

    @Before
    void setUp() {
        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()
        project.apply plugin: ReleasePlugin

        defaultPluginTask = project.tasks.getByName( ReleasePlugin.PREPARE_RELEASE_TASK ) as DefaultPluginTask
    }

    @Test
    void 'get version returns standard project version'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'

        then:
        assertEquals( '1.0.0-SNAPSHOT', defaultPluginTask.projectVersion )
    }

    @Test( expected = UnspecifiedVersionException.class )
    void 'getting unspecified version throws exception'() {
        when:
        defaultPluginTask.projectVersion
    }

    @Test
    void 'set version should update build file'() {
        setup:
        def buildFile = temporaryFolder.newFile( 'build.gradle' )
        buildFile << 'version=1.0.0-SNAPSHOT'

        project.version = '1.0.0-SNAPSHOT'

        when:
        defaultPluginTask.projectVersion = '1.0.0'

        then:
        def buildFileAsProperties = new Properties()
        buildFile.withInputStream { buildFileAsProperties.load( it ) }

        assertEquals( '1.0.0', buildFileAsProperties.version )
    }

    @Test
    void 'set version should update properties file'() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile << 'version=1.0.0-SNAPSHOT'

        project.version = '1.0.0-SNAPSHOT'

        when:
        defaultPluginTask.projectVersion = '1.0.0'

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.0', properties.version )
    }

    @Test
    void 'set version should not result in an error when files are missing'() {
        setup:
        project.version = '1.0.0-SNAPSHOT'

        when:
        defaultPluginTask.projectVersion = '1.0.0'

        then:
        assertEquals( '1.0.0', project.version )
    }

}
