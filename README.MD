# Gradle Release Plugin


## Use cases

- prepare/verify/check release
- set snapshot

- build (default task)


3.0-SNAPSHOT -> 3.0 -> after build: commit + tag -> 3.1-SNAPSHOT -> commit

3.0 -> after build: tag -> 3.1 -> commit


## Requirements

- Allow people to hook in tasks between our tasks (pre/post tagging)


## Prepare Release Task
- Verify dependencies
- Verify SCM
- Set release version (if is snapshot version)

## Release Task
- Depends on Build Task?
- What will it do?

## Save Release Task
- Commit released version and all modified files
- Tag previous commit

- Increment version for next (snapshot) release
- Commit new version



## What if release fails?
- Roll back version modifications in file?
See maven release plugin

