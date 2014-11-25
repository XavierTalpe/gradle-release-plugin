package be.xvrt.gradle.plugin.release.util

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class GradlePropertiesTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Project project
    private GradleProperties gradleProperties

    @Before
    void setUp() {
        project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()

        gradleProperties = new GradleProperties( project )
    }

    @Test
    void 'properties file should be updated with new version'() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile << 'version=1.0.0-SNAPSHOT'

        when:
        gradleProperties.updateVersion '1.0.0-SNAPSHOT', '1.0.0'

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.0', properties.version )
    }

    @Test
    void 'properties file with many lines should only have version line updated'() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile << 'name=aversion\n'
        propertiesFile << 'version=1.0.0-SNAPSHOT\n\n'
        propertiesFile << 'group=be.xvrt\n'

        when:
        gradleProperties.updateVersion '1.0.0-SNAPSHOT', '1.0.0'

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( 'aversion', properties.name )
        assertEquals( '1.0.0', properties.version )
        assertEquals( 'be.xvrt', properties.group )
    }

    @Test
    void 'properties file with indirect version should also be updated'() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile << 'name=1.0.0-SNAPSHOT\n'
        propertiesFile << 'version=name\n\n'
        propertiesFile << 'group=be.xvrt\n'

        when:
        gradleProperties.updateVersion '1.0.0-SNAPSHOT', '1.0.0'

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.0', properties.name )
        assertEquals( 'name', properties.version )
        assertEquals( 'be.xvrt', properties.group )
    }

    @Test
    void 'all version occurrences should be replaced'() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile << 'name=1.0.0-SNAPSHOT\n'
        propertiesFile << 'version=1.0.0-SNAPSHOT\n\n'
        propertiesFile << 'group=be.xvrt\n'

        when:
        gradleProperties.updateVersion '1.0.0-SNAPSHOT', '1.0.0'

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.0', properties.name )
        assertEquals( '1.0.0', properties.version )
        assertEquals( 'be.xvrt', properties.group )
    }

    @Test
    void 'saving version should not result in error when properties and build file are missing'() {
        when:
        gradleProperties.updateVersion '1.0.0-SNAPSHOT', '1.0.0'
    }

    @Test
    void 'build file should be updated with new version'() {
        setup:
        def buildFile = temporaryFolder.newFile( 'build.gradle' )
        buildFile << 'version=1.0.0-SNAPSHOT'

        when:
        gradleProperties.updateVersion '1.0.0-SNAPSHOT', '1.0.0'

        then:
        def buildFileAsProperties = new Properties()
        buildFile.withInputStream { buildFileAsProperties.load( it ) }

        assertEquals( '1.0.0', buildFileAsProperties.version )
    }

}
