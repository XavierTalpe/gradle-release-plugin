# Gradle Release Plugin [![Travis](https://travis-ci.org/XavierTalpe/gradle-release-plugin.svg?branch=master)](https://travis-ci.org/XavierTalpe/gradle-release-plugin)

Plugin that automatically prepares and increments the project version when making releases.

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
        classpath 'be.xvrt:release-plugin:0.3.0'
    }
}

apply plugin: 'be.xvrt.release'
```

Alternatively you can also use the new, incubating, plugin mechanism introduced in Gradle 2.1:
```
plugins {
    id 'be.xvrt.release' version '0.3.0'
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
- Passing in a parameter `-Pversion=XXX` from the command line when invoking Gradle, useful for parametrized CI builds.
- Set the version in your `build.gradle` file. This version number **won't** be updated and committed if the release was successful.
- Add a `version` property to your `gradle.properties` file. This version number **will** be updated and committed if the release was successful.


## Limitations
* Gradle 2.0 and up. Lower versions have not (yet) been tested.
* Currently only supports GIT as SCM.
* Not tested in combination with the Android plugin.
* Feel free to help in solving these limitations!


## Configuring the plugin
The **release** task is the main entry point for configuring this plugin. The below table shows all **configurable properties**, what they are for and what their default value is.

Property | Description
--- | ---
releaseVersion | TODO
nextVersion | TODO
|
scmDisabled | TODO
scmRootDir | The root directory where the SCM is initialized. For GIT this is the directory containing a .git directory. Defaults to the project root directory.
scmRemote | The name of the remote repository to push to when committing/tagging SCM changes. Only used for GIT. Defaults to origin.
|
releaseCommitMessage | TODO
releaseTag | TODO
releaseTagMessage | TODO
updateVersionCommitMessage | TODO

As an example of how to configure any of the above properties, the following snippet overrides the `releaseCommitMessage` property with a commit message that mentions the project name. It also configures a closure to customize the release version:

```
release {
    releaseCommitMessage = "${project.name} v%version."

    releaseVersion = { version ->
        version -= '-SNAPSHOT'
        version += '-RC1'

        version
    }
}
```

More **detailed examples** of how to configure this plugin can be found in the [examples](https://github.com/XavierTalpe/gradle-release-plugin/tree/master/examples) folder of this project's root directory.


## Tasks API

### Overview
Task | Description
--- | ---
prepareRelease | Sets the release version before the release build is started.
commitRelease | TODO
tagRelease | TODO
updateVersion | Sets the version for the next snapshot build and commits this change to the SCM.
release | Parent task of this plugin. Ensures all other tasks are executed at the right time.


### prepareRelease
Before starting the actual build, the prepareRelease task will remove the `-SNAPSHOT` tag from the current version number (if present). For example, if the version is `1.2-SNAPSHOT`, a `1.2` release will be made.

The project `version` can either be directly declared in the `build.gradle` file or implicitly via a `gradle.properties` file. If a `gradle.properties` file is present and it already contains a `version` property, then this value will be overwritten (and saved) during the release process. If no such file (or property) is present, no attempt will be made to create (or update) a `gradle.properties` file.

The release task also offers an extensible closure to insert custom logic when setting the release version. The example below demonstrates how to implement a closure that adds a suffix to the current version.
```
release {
    releaseVersion = { version ->
        version + 'RC1'
    }
}
```

### tagRelease
If the release was successfully created, the current state of the project (all modified files) will be committed and tagged.

TODO
- Add example to tag only after uploading archives

### updateVersion
After the build is successfully created, the updateVersion task is responsible for incrementing the version number so it's ready for the next (snapshot) build. If the original version was a snapshot release, then the new version will also be a snapshot release, otherwise only the version number is incremented. This version number will also be committed to the SCM.

The release task offers an extensible closure to insert custom logic when setting the next release or snapshot version. The example below demonstrates how to implement a closure that returns 2.0-SNAPSHOT as next snapshot release.
```
release {
    nextVersion = { version, wasSnapshotVersion ->
        '2.0-SNAPSHOT'
    }
}
```

### release
The release tasks is responsible for wiring all the other tasks together. It also ensures `prepareRelease` is executed before the `build` task while all other tasks are executed later.

By default the release task will use the `build` task to wire everything together. This task is typically provided by external plugins such as the Java plugin. To override this behaviour, use the following code:
```
tasks.release.dependsOn customBuild
tasks.tagRelease.dependsOn customBuild
tasks.updateVersion.dependsOn customBuild

customBuild.mustRunAfter tasks.prepareRelease
```
