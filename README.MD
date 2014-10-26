# Gradle Release Plugin [![Travis](https://travis-ci.org/XavierTalpe/gradle-release-plugin.svg?branch=master)](https://travis-ci.org/XavierTalpe/gradle-release-plugin)

*Note: this is a work in progress plugin for Gradle. It's functionality is currently very limited.*

The release plugin automatically takes care of versioning your relases. Before starting the build, the '-SNAPSHOT' tag  will be removed from the version number (if any). After the build is sucessfully created, the current state of the project will be committed and tagged. Finally, the version number will be incremented and initialized for the next (snapshot) release.

This plugin is mostly an attempt for me to get more knowledge about Gradle. Although inspired by the release plugin of [Eric Berry](https://github.com/townsfolk/gradle-release), this project is not a fork and was written from the ground up.

## Task

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
