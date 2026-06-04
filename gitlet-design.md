# Gitlet Design Document

**Name**:

## Classes and Data Structures

### Class Repository

#### Fields

1. File to the objects/refs folder
2. File to the master file


### Class Commit

#### Fields

1. Field 1
2. Field 2


## Algorithms


## Persistence


## Architecture

### New architecture /modifications to be made
- make a new directory objects/refs
- inside refs store the names of the branches (since branches are nothing 
but just references to a commit)
- make the HEAD point to the "master" branch on init command
- instead of editing HEAD like we have been doing till now, just update the "master" pointer (or any other branch that you're on) to point to the latest commit
- branch name always points to the latest commit of that branch
- **IMPORTANT:** In a normal state, `HEAD` points to a branch (like `master`), but in a detached HEAD state, `HEAD` points directly to a commit. Even if both `HEAD` and `master` point to the same commit, you are still detached because `HEAD` is not pointing to the branch itself.
- somehow distinguish between if the `HEAD` is directly pointing to a commit or to a branch
- `master`will now directly point to the commits from now on
