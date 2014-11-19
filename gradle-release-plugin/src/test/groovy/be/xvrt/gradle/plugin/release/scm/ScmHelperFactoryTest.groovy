package be.xvrt.gradle.plugin.release.scm

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertTrue

class ScmHelperFactoryTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test
    void 'git folder requires git helper'() {
        setup:
        temporaryFolder.newFolder '.git'

        when:
        def scmHelper = ScmHelperFactory.create temporaryFolder.root

        then:
        assertTrue( scmHelper instanceof GitHelper )
    }

    @Test
    void 'no scm folder requires dummy helper'() {
        when:
        def scmHelper = ScmHelperFactory.create temporaryFolder.root.absolutePath

        then:
        assertTrue( scmHelper instanceof DummyHelper )
    }

    @Test
    void 'identical directories should return the same helper'() {
        when:
        def scmHelper1 = ScmHelperFactory.create temporaryFolder.root
        def scmHelper2 = ScmHelperFactory.create temporaryFolder.root

        then:
        assertTrue( scmHelper1 == scmHelper2 )
    }

}
