package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.scm.Tag
import be.xvrt.gradle.plugin.task.AbstractScmTask

class TagReleaseTask extends AbstractScmTask {

    private Tag tagId

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info ":${name} skipping tagRelease because SCM support is disabled."
        }
        else {
            tagId = tag()
            push()
        }
    }

    @Override
    void rollback( Exception exception ) {
        rollbackTag tagId
    }

    private Tag tag() {
        def tagName = extension.getAt ReleasePluginExtension.RELEASE_TAG
        def tagMessage = extension.getAt ReleasePluginExtension.RELEASE_TAG_MSG
        def releaseVersion = projectVersion

        tag tagName, tagMessage, releaseVersion
    }

    private void rollbackTag( Tag tagId ) throws ScmException {
        if ( tagId ) {
            logger.info ":${name} rolling back tag due to error."

            scmHelper.deleteTag tagId
        }
    }

}
