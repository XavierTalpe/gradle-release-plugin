package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.scm.Tag
import be.xvrt.gradle.plugin.task.PluginScmTask

class TagReleaseTask extends PluginScmTask {

    private Tag tagId

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info ":${name} skipping tagRelease because SCM support is disabled."
        }
        else {
            tagId = tag()
            pushTag tagId
        }
    }

    @Override
    void rollback( Exception exception ) {
        rollbackTag tagId
    }

    private Tag tag() {
        def tagName = extension.getAt( ReleasePluginExtension.RELEASE_TAG ) as String
        def tagMessage = extension.getAt( ReleasePluginExtension.RELEASE_TAG_MSG ) as String
        def releaseVersion = projectVersion

        tag tagName, tagMessage, releaseVersion
    }

    private void rollbackTag( Tag tagId ) throws ScmException {
        if ( tagId ) {
            logger.info ":${name} rolling back unpushed tag due to error."

            scmHelper.deleteTag tagId
        }
    }

}
