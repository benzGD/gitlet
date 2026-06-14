# Gitlet

Gitlet is a version-control system implemented in Java as part of UC Berkeley's CS61B course project. It mimics a small but functional subset of Git, including committing, branching, checkout, merging, and log history.

## Commands
### **1. `init`**
- **Usage**:  
  ```sh
  java gitlet.Main init
  ```
- **Description**:  
  Initializes a new Gitlet repository in the current directory.

### **2. `add`**
- **Usage**:  
  ```sh
  java gitlet.Main add [file name]
  ```
- **Description**:  
  Stages the file for addition.

### **3. `commit`**
- **Usage**:  
  ```sh
  java gitlet.Main commit [message]
  ```
- **Description**:  
  Saves a snapshot of the current state.

### **4. `rm`**
- **Usage**:  
  ```sh
  java gitlet.Main rm [file name]
  ```
- **Description**:  
  Removes a file from staging or tracking.

### **5. `log`**
- **Usage**:  
  ```sh
  java gitlet.Main log
  ```
- **Description**:  
  Displays commit history.

### **6. `global-log`**
- **Usage**:  
  ```sh
  java gitlet.Main global-log
  ```
- **Description**:  
  Displays all commits.

### **7. `find`**
- **Usage**:  
  ```sh
  java gitlet.Main find [commit message]
  ```
- **Description**:  
  Finds commits with the given message.

### **8. `status`**
- **Usage**:  
  ```sh
  java gitlet.Main status
  ```
- **Description**:  
  Displays branches, staged files, and untracked files.

### **9. `checkout`**
- **Usages**:
  ```sh
  java gitlet.Main checkout -- [file name]
  java gitlet.Main checkout [commit id] -- [file name]
  java gitlet.Main checkout [branch name]
  ```
- **Description**:  
  Restores files from a commit or branch.

### **10. `branch`**
- **Usage**:  
  ```sh
  java gitlet.Main branch [branch name]
  ```
- **Description**:  
  Creates a new branch.

### **11. `rm-branch`**
- **Usage**:  
  ```sh
  java gitlet.Main rm-branch [branch name]
  ```
- **Description**:  
  Deletes a branch.

### **12. `reset`**
- **Usage**:  
  ```sh
  java gitlet.Main reset [commit id]
  ```
- **Description**:  
  Moves the current branch to a specified commit.

### **13. `merge`**
- **Usage**:  
  ```sh
  java gitlet.Main merge [branch name]
  ```
- **Description**:  
  Merges another branch into the current branch.

## About this project

Gitlet is a version-control system implemented in Java as part of UC Berkeley’s CS61B course project. It mimics a small but functional subset of Git, including committing, branching, checkout, merging, and log history.

This was one of the first large-scale software engineering projects I completed. Unlike smaller assignments that guide you step by step, Gitlet gives you only a full specification, and you have to design and implement the system yourself. That made the project much more challenging, but also much more rewarding.

Working on Gitlet taught me a lot about object-oriented design, function and class naming, organizing a larger codebase, working with file I/O and serialization, and handling edge cases carefully. It also showed me how important design decisions are early on. A poor design can easily cost many hours later when the project grows and new commands depend on the old ones.

One of the biggest challenges was the `merge` command. It is the longest and most complicated part of the project, and it depends on nearly every previous command being correct. If earlier commands are even slightly wrong, merge becomes very difficult to debug. In that sense, the project behaves a lot like a state machine: many different file states and branch states must be handled correctly, and the autograder checks those cases very thoroughly.

I also learned that trying to put everything into one class makes the project much harder to manage. Breaking the work into separate classes and helper methods made the design much cleaner and easier to reason about.

I received a full score of **1600/1600** on the main autograder for this project.
<img width="348" height="1256" alt="Screenshot 2026-06-14 101117" src="https://github.com/user-attachments/assets/423b7b2a-53f6-44b5-9a42-d91f0bdb5391" />


## Features implemented

- `init` — initialize a new Gitlet repository
- `add` — stage files for addition
- `commit` — save snapshots of tracked files
- `rm` — stage files for removal
- `log` and `global-log` — view commit history
- `find` — search commits by message
- `status` — show repository state
- `checkout` — restore files or switch branches
- `branch` and `rm-branch` — manage branches
- `reset` — move the current branch to an earlier commit
- `merge` — merge changes from another branch into the current branch

## Implementation highlights

Some of the more interesting parts of the project were:

- storing commits and blobs using SHA-1 hashes
- representing commit history as a directed acyclic graph
- finding split points for merges
- handling merge conflicts
- keeping track of staged additions and removals
- making the implementation work correctly with serialized files on disk

## Challenges

The hardest part of the project was the `merge` command. It depended on the correctness of nearly every other command, and required careful handling of many file states and edge cases. Good design decisions early on saved a lot of time later.

## Summary
Gitlet is a project of about 1,000 lines(Although my implementation ended up being appox ~ 1.5k loc). It is not very large, but it contains a wealth of knowledge points and ideas. This project can fully train beginners to implement a just-right project.

I gained a lot from this project. For example, my understanding of function naming, object-oriented ideas, functional programming methods, and induction and organization skills have all been improved. Good design decisions early on can save dozens of hours of work later, while poor design choices can easily set a project back by 10–30 hours or more.

Thank you, UCB, for making the project specifications and autograder publicly available. Thank you, Professor Josh, for creating such a valuable learning experience.
