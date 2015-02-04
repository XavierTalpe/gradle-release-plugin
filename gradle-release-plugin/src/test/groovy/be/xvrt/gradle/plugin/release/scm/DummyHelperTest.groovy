package be.xvrt.gradle.plugin.release.scm

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DummyHelperTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test
    void 'dummy helper does nothing'() {
        def dummyHelper = ScmHelperFactory.create temporaryFolder.root

        def commitId = dummyHelper.commit 'commitMessage'
        dummyHelper.deleteCommit commitId

        def tagId = dummyHelper.tag '1.0.0', 'tagMessage'
        dummyHelper.deleteTag tagId

        dummyHelper.push 'origin'
        dummyHelper.pushTag 'origin', tagId
    }

}
