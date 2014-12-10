# Gradle Release Plugin [![Travis](https://travis-ci.org/XavierTalpe/gradle-release-plugin.svg?master)](https://travis-ci.org/XavierTalpe/gradle-release-plugin)

Automatically prepares and increments the version number before and after making a release. Commits and tags releases as well.

Before starting the build, the `SNAPSHOT` tag (if present) will be removed from the current project version. After the build is successfully created, the current version of the project will be committed and tagged. Finally, the version number will be incremented and initialized for the next (SNAPSHOT) release.


## How to use this plugin

### Apply the plugin to your project
Simply add the following snippet to the top of your Gradle file:
``` 
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'be.xvrt:release-plugin:0.4.1'
    }
}

apply plugin: 'be.xvrt.release'
```

Alternatively you can also use the new, incubating, plugin mechanism introduced in Gradle 2.1:
```
plugins {
    id 'be.xvrt.release' version '0.4.1'
}
```


### Execute the release task
To trigger the release process (including automated commits and tagging!), simply run:
```
$ gradle release
```

This will automatically trigger an existing `build` task and ensure the correct version is used to build the release artifacts. The release will be committed and tagged. Preparations for the next release will also be committed.

For projects that don't have a build task or need to modify the default behaviour, see the [release](#release) task documentation.

### Specifying the (release) version
There are multiple options to provide the version for building the project. **In order of priority**:
- Pass in a parameter `-Pversion=XXX` from the command line when invoking Gradle, useful for parametrized CI builds.
- Set a `version` property in your `build.gradle` file. This version number will be updated and committed if the release was successful.
- Add a `version` property to your `gradle.properties` file. This version number will be updated and committed if the release was successful.


## Limitations
* Gradle 2.0 and up. Lower versions have not (yet) been tested.
* Currently only supports GIT as SCM.
* Not tested in combination with the Android plugin.
* Feel free to help in solving these limitations!


## Configuring the plugin

The default settings of this plugin can be overridden using both task properties and command line parameters. The following sections explain this functionality.

### Build file configuration

The **release** task is the main entry point for configuring this plugin from your `build.gradle` file. The below table shows all **configurable properties**, what they are for and what their default value is.

Property | Description
--- | ---
releaseVersion | Closure containing the logic to build the release version. By default removes a trailing `-SNAPSHOT` tag from the initial version (if present).
nextVersion | Closure containing the logic to build the next (`SNAPSHOT`) version. By default adds `-SNAPSHOT` to the release version if the initial version also contained `SNAPSHOT`.
allowSnapshotDependencies | Toggle to allow `SNAPSHOT` project dependencies when building the release. False by default.
scmDisabled | Disables all commit, tagging and pushing functionality. Effectively makes it look as if the project does not use an SCM. Useful for testing or demonstration purposes. Defaults to `false`.
scmRootDir | The root directory where the SCM is initialized. For GIT this is the directory containing a `.git` directory. Defaults to the project's root directory.
scmRemote | The name of the remote repository to push to when committing or tagging SCM changes. Only used for GIT. Defaults to `origin`.
scmUsername | The username to use when interacting with the SCM. Defaults to system username.
scmPassword | The password to use when interacting with the SCM. Defaults to system password.
releaseCommitMessage | Commit message to save the project state after building the release. Defaults to `[Gradle Release] Commit for %version.`.
releaseTag | Tag (name) to annotate the project state after building the release. Defaults to `%version`.
releaseTagMessage | Tag message for the `releaseTag` property. Defaults to `[Gradle Release] Tag for %version.`.
updateVersionCommitMessage | Commit message to prepare the project state for the next build. Defaults to `[Gradle Release] Preparing for %version.`.

As an example of how to configure any of the above properties, the following snippet overrides the `releaseVersion` closure to customize the release version logic. The `releaseCommitMessage` property is replaced with a commit message that mentions the project name:

```
release {
    releaseVersion = { version ->
        version -= '-SNAPSHOT'
        version += '-RC1'

        version
    }
    
    releaseCommitMessage = "${project.name} v%version."
}
```

More **detailed examples** of how to configure this plugin can be found in the [examples](https://github.com/XavierTalpe/gradle-release-plugin/tree/master/examples) folder.

### Command line parameters

Limited configuration is also possible using command line parameters. These can be very useful when running parametrized builds from your CI. The below table shows all **configurable parameters**. Note that these command line parameters take precedence over the task properties mentioned in the previous section.

Parameter | Description
--- | ---
nextVersion | Set the version for the next (`SNAPSHOT`) build.
scmUsername | The username to use when interacting with the SCM.
scmPassword | The password to use when interacting with the SCM.

As an example, imagine that our current project has version `1.5.0-SNAPSHOT`. Running the `release` task will thus result in a `1.5.0` build being made. The version for the next build will be set to `1.5.1-SNAPSHOT`. However, we would like to modify this behaviour such that the version for the next build is set to `2.0.0-SNAPSHOT` instead. To do this, simply pass the `nextVersion` parameter when running gradle from the command line:
```
$ gradle release -PnextVersion=2.0.0-SNAPSHOT
```


## Tasks API

### Overview
Task | Description
--- | ---
prepareRelease | Checks for SNAPSHOT dependencies and sets the release version before the build is started.
commitRelease | Commits any file changes for this release to the SCM.
tagRelease | Tags this release to the SCM.
updateVersion | Sets the version for the next snapshot build and commits this change to the SCM.
release | Parent task of this plugin. Ensures all other tasks are executed at the right time.


TODO
- Add example to commit only after uploading archives

### prepareRelease
Before starting the actual build, the `prepareRelease` task will remove the `-SNAPSHOT` tag from the current version number (if present). For example, if the version is `1.2-SNAPSHOT`, a `1.2` release will be made.

There are three options to provide the project version. These are, in order of priority:
- Pass in a parameter `-Pversion=XXX` from the command line.
- Set a `version` property in your `build.gradle` file.
- Add a `version` property to your `gradle.properties` file.

If the `version` property was defined in the `build.gradle` file, than this value will be updated during the release process. If a `gradle.properties` file is present and it already contains a `version` property, then this value will be updated and committed during the release process. If no such file (or property) is present, no attempt will be made to create (or update) the `gradle.properties` file.

Using the `release` task it's also possible to override the logic to configure the release version. The following code snippet demonstrates how to implement a closure that adds an `RC1` suffix to the current version:
```
release {
    releaseVersion = { version ->
        version + 'RC1'
    }
}
```

The `prepareRelease` task also checks if there are any remaining `-SNAPSHOT` dependencies in the project. If this is the case, the release will be halted. This feature can be disabled by setting the `allowSnapshotDependencies` property to false:
```
release {
    allowSnapshotDependencies = true
}
```

### commitRelease
Executed if the build was successful. Takes care of committing all changes files (build.gradle, gradle.properties and other) to the SCM. The commit message for this task can be overridden by specifying the `releaseCommitMessage` property. See the [custom messages example](https://github.com/XavierTalpe/gradle-release-plugin/tree/master/examples/custom-messages) for more information.

### tagRelease
Executed if the build was successful. Takes care of tagging the current release. The tag name and tag message for this task can be overridden by specifying the `releaseTag` or `releaseTagMessage` property. See the [custom messages example](https://github.com/XavierTalpe/gradle-release-plugin/tree/master/examples/custom-messages) for more information.

### updateVersion
Executed if the build was successful. The `updateVersion` task is responsible for incrementing the version number so it's ready for the next (snapshot) build. If the original version was a snapshot release, then the new version will also be a snapshot release, otherwise the version number is only incremented. This new version number will also be committed to the SCM.

Using the `release` task it's also possible to override the logic to configure the next version. The code snippet below demonstrates how to implement a closure that returns `2.0-SNAPSHOT` as next snapshot release.
```
release {
    nextVersion = { version, wasSnapshotVersion ->
        '2.0-SNAPSHOT'
    }
}
```

### release
The release tasks is responsible for wiring all the other tasks together. It also ensures `prepareRelease` is executed before the `build` task while all other tasks are executed later.

By default the release task will use the `build` task to wire everything together. This task is typically provided by external plugins such as the Java plugin. In case no such task is available, you can manually connect the `release` with another `customBuild` task:
```
tasks.release.dependsOn customBuild
tasks.commitRelease.dependsOn customBuild
tasks.tagRelease.dependsOn customBuild
tasks.updateVersion.dependsOn customBuild

customBuild.mustRunAfter tasks.prepareRelease
```
