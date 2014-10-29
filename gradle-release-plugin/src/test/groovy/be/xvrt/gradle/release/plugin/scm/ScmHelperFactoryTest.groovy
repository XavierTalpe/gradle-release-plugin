package be.xvrt.gradle.release.plugin.scm

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertTrue

class ScmHelperFactoryTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    void testCreateGitScmHelper() {
        setup:
        temporaryFolder.newFolder( '.git' )

        when:
        def scmHelper = ScmHelperFactory.create( temporaryFolder.root )

        then:
        assertTrue( scmHelper instanceof GitHelper )
    }

    @Test
    void testCreateDummyScmHelper() {
        when:
        def scmHelper = ScmHelperFactory.create( temporaryFolder.root.getAbsolutePath() )

        then:
        assertTrue( scmHelper instanceof DummyHelper )
    }

    @Test
    void testCache() {
        when:
        def scmHelper1 = ScmHelperFactory.create( temporaryFolder.root )
        def scmHelper2 = ScmHelperFactory.create( temporaryFolder.root )

        then:
        assertTrue( scmHelper1 == scmHelper2 )
    }

}
