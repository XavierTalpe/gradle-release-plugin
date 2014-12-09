package be.xvrt.gradle.plugin.release.integration

import be.xvrt.gradle.plugin.test.IntegrationTest
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertEquals

class ReleaseTaskTest extends IntegrationTest {

    @Test
    void 'default project is successfully released'() {
        setup:
        addProperty 'version', '1.0.0-SNAPSHOT'
        cloneGitRepository()

        when:
        execute 'release'

        then:
        assertEquals '1.0.1-SNAPSHOT', gradleProperties.version
        assertCommits remoteRepository
        assertTag remoteRepository
    }

    @Test
    void 'java project is successfully released'() {
        setup:
        addProperty 'version', '1.0.0-SNAPSHOT'
        appendLineToBuildFile 'apply plugin: "java"'
        cloneGitRepository()

        when:
        execute 'release'

        then:
        assertEquals '1.0.1-SNAPSHOT', gradleProperties.version
        assertCommits remoteRepository
        assertTag remoteRepository
    }

    @Ignore
    @Test
    void 'android project is successfully released'() {
        // TODO
    }

    private static void assertCommits( Repository repository ) {
        def commitLog = new Git( repository ).log().call().toList()

        assertEquals( 3, commitLog.size() )
        assertEquals( 'HEAD', commitLog.get( 2 ).shortMessage )
        assertEquals( '[Gradle Release] Commit for 1.0.0.', commitLog.get( 1 ).shortMessage )
        assertEquals( '[Gradle Release] Preparing for 1.0.1-SNAPSHOT.', commitLog.get( 0 ).shortMessage )
    }

    private static void assertTag( Repository repository ) {
        def tagList = new Git( repository ).tagList().call()

        assertEquals( 1, tagList.size() )
        assertEquals( 'refs/tags/1.0.0', tagList.get( 0 ).getName() )
    }

    @Test
    void 'passing username and password from console does not throw error'() {
        setup:
        addProperty 'version', '1.0.0-SNAPSHOT'
        cloneGitRepository()

        when:
        execute 'release -PscmUsername=username -PscmPassword=password'

        then:
        assertEquals '1.0.1-SNAPSHOT', gradleProperties.version
        assertCommits remoteRepository
        assertTag remoteRepository
    }

}
