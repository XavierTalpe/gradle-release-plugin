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
    classpath 'be.xvrt:release-plugin:0.1.2'
  }
}

apply plugin: 'be.xvrt.release'
```

Alternatively you can also use the new, incubating, plugin mechanism introduced in Gradle 2.1:
```
plugins {
  id 'be.xvrt.release' version '0.1.2'
}
```


### Execute the release task
TODO


### Specifying the version
- Either from a gradle.properties file (in which case we update it)
- By passing parameter -Pversion from command line (in which case we won't update the file (unless there is one))


### Configuring the plugin
By extending the release task it's possible to configure various options for the release task. The below table shows all configurable properties, what they are for and what their default value is.

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


## Limitations
* Gradle 2.0 and up. Lower versions have not (yet) been tested.
* Currently only supports GIT as SCM.


## Tasks API

### Overview
Task | Description
--- | ---
prepareRelease | Sets the release version before the release build is started.
tagRelease | TODO
prepareNextRelease | TODO
release | Parent task of this plugin. Ensures all other tasks are executed at the right time.


### prepareRelease
Before starting the actual build, the prepareRelease task will remove the '-SNAPSHOT' tag from the current version number (if present). For example, if the version is 1.2-SNAPSHOT, a *1.2* release will be made.

The project `version` can either be directly declared in the `build.gradle` file or implicitly via a `gradle.properties` file. If a `gradle.properties` file is present and it already contains a `version` property, then this value will be overwritten (and saved) during the release process. If no such file (or property) is present, no attempt will be made to create (or update) a `gradle.properties` file.


### tagRelease
After the build is successfully created, the current state of the project will be committed and tagged. Finally, the version number will be incremented and initialized for the next (snapshot) release.

- Commit released version and all modified files
- Tag previous commit
- Add example to tag only after uploading archives

### prepareNextRelease
- Increment version for next (snapshot) release
- Commit new version


### release
The release tasks is responsible for wiring all the other tasks together. It also ensures `prepareRelease` is executed before the `build` task while all other tasks are executed later.

By default the release task will use the `build` task to wire everything together. This task is typically provided by external plugins such as the Java plugin. To override this behaviour, use the following code:
```
release.dependsOn customBuild
tagRelease.dependsOn customBuild
prepareNextRelease.dependsOn customBuild

customBuild.mustRunAfter prepareRelease
```
