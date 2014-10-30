# Gradle Release Plugin [![Travis](https://travis-ci.org/XavierTalpe/gradle-release-plugin.svg?branch=master)](https://travis-ci.org/XavierTalpe/gradle-release-plugin)

The release plugin automatically takes care of versioning your relases. Before starting the build, the '-SNAPSHOT' tag  will be removed from the version number (if any). After the build is sucessfully created, the current state of the project will be committed and tagged. Finally, the version number will be incremented and initialized for the next (snapshot) release.

This plugin is mostly an attempt for me to get more knowledge about Gradle. Although inspired by the release plugin of [Eric Berry](https://github.com/townsfolk/gradle-release), this project is not a fork and was written from the ground up.

## How to use this plugin

### Apply the plugin to your project

Simply add the following snippet to the top of your Gradle file:
``` 
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'be.xvrt:release-plugin:0.1.+'
  }
}

apply plugin: 'be.xvrt.release'
```

Alternatively you can also use the new, incubating, plugin mechanism introduced in Gradle 2.1:
```
plugins {
  id 'be.xvrt.release' version '0.1.1'
}
```

### Execute the release task

TODO

## Requirements

This plugin has been tested on Gradle 2.1 and up. Lower versions have not been tested. Feel free to log an issue should you come accross a problem with an older version.


## Tasks

### Overview

Task name | Description | Status
--- | --- | ---
prepareRelease | TODO | In progress
release | TODO | In progress
commitRelease | TODO | Not started
tagRelease | TODO | Not started
prepareNextRelease | TODO | Not started

### prepareRelease
- Verify dependencies
- Verify SCM
- Set release version (if is snapshot version)

### release
- Depends on build task to trigger build
- Wires our tasks together

#### commitRelease
- Commit released version and all modified files

### tagRelease
- Tag previous commit

### prepareNextRelease
- Increment version for next (snapshot) release
- Commit new version

# TODOS
## What if release fails?
- Roll back version modifications in file?

## How to upgrade to next major version?
For example, currently 2.1-SNAPSHOT. Next release should be for 3.0(-SNAPSHOT).
- Allow parameter to set next version

## Setting the version
- Either from a gradle.properties file (in which case we update it)
- By passing parameter -Pversion from command line (in which case we won't update the file (unless there is one))
