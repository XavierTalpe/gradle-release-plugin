package be.xvrt.gradle.plugin.release.integration

import be.xvrt.gradle.plugin.test.IntegrationTest
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class PrepareReleaseTaskTest extends IntegrationTest {

    @Test
    void 'empty properties file remains empty after prepareRelease'() {
        setup:
        appendLineToBuildFile 'version="1.0.0-SNAPSHOT"'
        cloneGitRepository()

        when:
        execute 'prepareRelease'

        then:
        assertTrue gradleProperties.isEmpty()
    }

    @Test
    void 'properties file is updated after prepareRelease'() {
        setup:
        addProperty 'version', '1.0.0-SNAPSHOT'
        cloneGitRepository()

        when:
        execute 'prepareRelease'

        then:
        assertEquals '1.0.0', gradleProperties.version
    }

    @Test
    void 'properties file is rolled back when task fails'() {
        setup:
        addProperty 'version', '1.0.0-SNAPSHOT'
        appendLineToBuildFile 'release {'
        appendLineToBuildFile '  releaseVersion = "INVALID"'
        appendLineToBuildFile '}'

        cloneGitRepository()

        when:
        execute 'prepareRelease', true

        then:
        assertEquals '1.0.0-SNAPSHOT', gradleProperties.version
    }

}
