# Gradle Release Plugin ![](https://travis-ci.org/XavierTalpe/gradle-release-plugin.svg?branch=master)

This is a work in progress plugin for Gradle. It takes care of automatically
removing the SNAPSHOT tag from a version before making a release. After the
release is made, it will reintroduce the SNAPSHOT tag and also increment the version number.

Inspired by the release plugin of [Eric Berry](https://github.com/townsfolk/gradle-release).

## Prepare Release Task
- Verify dependencies
- Verify SCM
- Set release version (if is snapshot version)

## Release Task
- Depends on build task to trigger build
- Wires our tasks together

## Save Release Task
- Commit released version and all modified files
- Tag previous commit

- Increment version for next (snapshot) release
- Commit new version

# TODOS

## What if release fails?
- Roll back version modifications in file?
See maven release plugin


## How to upgrade to next major version?
For example, currently 2.1-SNAPSHOT. Next release should be for 3.0(-SNAPSHOT).
- Allow parameter to set next version



## Setting the version
- Either from a gradle.properties file (in which case we update it)
- By passing parameter -Pversion from command line (in which case we won't update the file (unless there is one))
