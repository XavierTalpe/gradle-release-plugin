package be.xvrt.gradle.plugin.release

import be.xvrt.gradle.plugin.release.scm.ScmException
import be.xvrt.gradle.plugin.release.scm.Tag
import be.xvrt.gradle.plugin.task.AbstractScmTask

class TagReleaseTask extends AbstractScmTask {

    private Tag tagId

    @Override
    void configure() {
    }

    @Override
    void run() {
        if ( isScmSupportDisabled() ) {
            logger.info "${name} skipping tagRelease because SCM support is disabled."
        }
        else {
            def extension = project.extensions.getByName ReleasePlugin.RELEASE_TASK

            def scmRemote = extension.getAt ReleasePluginExtension.SCM_REMOTE
            def tagName = extension.getAt ReleasePluginExtension.RELEASE_TAG
            def tagMessage = extension.getAt ReleasePluginExtension.RELEASE_TAG_MSG
            def releaseVersion = project.version

            tagId = tag tagName, tagMessage, releaseVersion
            push scmRemote
        }
    }

    @Override
    void rollback( Exception exception ) {
        rollbackTag()

        throw exception;
    }

    private void rollbackTag() throws ScmException {
        if ( tagId ) {
            logger.info "${name} rolling back tag due to error."

            getScmHelper().deleteTag tagId
        }
    }

}
