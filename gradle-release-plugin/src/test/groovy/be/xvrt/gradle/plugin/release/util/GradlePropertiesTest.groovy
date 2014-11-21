package be.xvrt.gradle.plugin.release.util

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

class GradlePropertiesTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private GradleProperties gradleProperties

    @Before
    void setUp() {
        def project = ProjectBuilder.builder().withProjectDir( temporaryFolder.root ).build()

        gradleProperties = new GradleProperties( project )
    }

    @Test
    void 'properties file should be overwritten with new version'() {
        setup:
        def propertiesFile = temporaryFolder.newFile( 'gradle.properties' )
        propertiesFile << 'version=1.0.0-SNAPSHOT'

        when:
        gradleProperties.saveVersion( '1.0.0' )

        then:
        def properties = new Properties()
        propertiesFile.withInputStream { properties.load( it ) }

        assertEquals( '1.0.0', properties.version )
    }

    @Test
    void 'saving version shouldn\'t result in error when properties file is missing'() {
        when:
        gradleProperties.saveVersion( '1.0.0' )
    }

}
