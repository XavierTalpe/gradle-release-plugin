package be.xvrt.gradle.plugin.release.integration

import be.xvrt.gradle.plugin.test.IntegrationTest
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class UpdateVersionTaskTest extends IntegrationTest {

    @Test
    void 'empty properties file remains empty after updateVersion'() {
        setup:
        appendToBuildFile 'version="1.0.0-SNAPSHOT"'

        when:
        execute 'updateVersion'

        then:
        def properties = getProperties()
        assertTrue properties.isEmpty()
    }

    @Test
    void 'properties file is updated after updateVersion'() {
        setup:
        addProperty 'version', '1.0.0-SNAPSHOT'

        when:
        execute 'updateVersion'

        then:
        def properties = getProperties()

        assertEquals '1.0.1-SNAPSHOT', properties.version
    }

    @Test
    void 'properties file is updated from command line'() {
        setup:
        addProperty 'version', '1.0.0-SNAPSHOT'

        when:
        execute 'release -PnextVersion=2.0.0-SNAPSHOT'

        then:
        def properties = getProperties()

        assertEquals '2.0.0-SNAPSHOT', properties.version
    }

}
