# Gradle Release Plugin [![Travis](https://travis-ci.org/XavierTalpe/gradle-release-plugin.svg?branch=master)](https://travis-ci.org/XavierTalpe/gradle-release-plugin)

This release plugin automatically takes care of versioning your releases. Before starting the build, the '-SNAPSHOT' tag  will be removed from the version number (if any). After the build is sucessfully created, the current state of the project will be committed and tagged. Finally, the version number will be incremented and initialized for the next (snapshot) release.

## How to use this plugin

### Apply the plugin to your project
Simply add the following snippet to the top of your Gradle file:
``` 
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'be.xvrt:release-plugin:0.2.2'
    }
}

apply plugin: 'be.xvrt.release'
```

Alternatively you can also use the new, incubating, plugin mechanism introduced in Gradle 2.1:
```
plugins {
    id 'be.xvrt.release' version '0.2.2'
}
```


### Execute the release task
To trigger a new release using this plugin, simply run
```
$ gradle release
```

This will automatically trigger an existing `build` task and ensure the correct version is used to build the release. For projects that don't have a build task or need to modify the default behaviour, see the [release](#release) task documentation.

### Specifying the version
There are multiple options to provide the version for building the project. **In order of priority**:
- Passing in a parameter `-Pversion=XXX` from the command line when invoking Gradle.
- Set the version in your `build.gradle` file.
- Add a `version` property to your `gradle.properties` file. If the build was successful, the updated version will also be stored in this file.


## Limitations
* Gradle 2.0 and up. Lower versions have not (yet) been tested.
* Currently only supports GIT as SCM.


## Configuring the plugin
By extending the release task it's possible to configure various options for this plugin. The below table shows all configurable properties, what they are for and what their default value is.

Property | Description
--- | ---
scmRootDir | The root directory where the SCM is initialized. For GIT this is the directory containing a .git directory. Defaults to the project root directory.
scmRemote | The name of the remote repository to push to when committing/tagging SCM changes. Only used for GIT. Defaults to origin.

As an example of how to configure any of the above properties, the following snippet overrides the `tagMessage` property with a commit message that mentions the project name.

```
release {
    tagMessage = "Tag for ${project.name} version "
}
```


## Tasks API

### Overview
Task | Description
--- | ---
prepareRelease | Sets the release version before the release build is started.
tagRelease | Commits and tags this release to the SCM.
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
