package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Branch.*;
import static gitlet.Commit.*;
import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author gd
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* The staging area directory. */
    public static final File INDEX_DIR = join(GITLET_DIR, "index");

    /*Staging for addition. */
    public static final File ADDITION_DIR = join(INDEX_DIR, "addition");


    /*Staged for removal directory.
     * Contains the names of the files to be staged for removal or deletion from
     * the CWD in the next commit */
    public static final File REMOVAL_DIR = join(INDEX_DIR, "removal");

    /* The commits directory. */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

    /*The objects directory. */
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");

    /*File pointing to the initial commit. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");  //edited

    /**
     * File pointing to the objects/refs folder...
     */
    public static final File REFS = join(OBJ_DIR, "refs");

    /**
     * Variable to store the master branch.
     * branch is just a pointer to a commit, nothing else...
     */
    private static final File MASTER = join(REFS, "master");


    /**
     * Variable to store the HEAD pointer.
     */
//    public static Commit HEAD;

    //use a string instead for HEAD and master variable????

//    public static String HEAD;

    // use a treemap datastructure from string to string and store it in somewhere????

    public static void setupRepository() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the"
                    + " current directory.");
            System.exit(0);

        }
        GITLET_DIR.mkdir();
        INDEX_DIR.mkdir();
        ADDITION_DIR.mkdir();
        REMOVAL_DIR.mkdir();
        COMMITS_DIR.mkdir();
        OBJ_DIR.mkdir();
        REFS.mkdir();
        try {
            HEAD.createNewFile();
            MASTER.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeContents(HEAD, "master");  //HEAD initially points to the master branch

        Commit head = new Commit("initial commit", null, null);
        head.saveCommmit();


    }

    public static boolean isIdentical(File f1, File f2) {
        String s1 = sha1((Object) readContents(f1));
        String s2 = sha1((Object) readContents(f2));
        return s1.equals(s2);
    }


    /*
     * If the current working version of the file is identical to the version
     * in the current commit, do not stage it to be added, and remove it from
     * the staging area if it is already there */
    public static void add(String name, File f) {
        File newfile = join(ADDITION_DIR, name);
//        System.out.println(HEAD);
        Commit curr = Commit.fromFile(HEAD); //fix this

        if (!newfile.exists()) {
            //first time adding or ....
            if (curr.getBlobmap() != null) {
                // check if blobmap contains the name or not
                if (curr.getBlobmap().containsKey(name)) {

                    //added
                    File fileToBeRemoved = join(REMOVAL_DIR, name);
                    if ((curr.getBlobmap().get(name)).equals(sha1((Object) readContents(f)))
                            && isExist(fileToBeRemoved)) {
                        fileToBeRemoved.delete();  //delete it from the removal dir
                        return;

                    } else if ((curr.getBlobmap().get(name)).equals(
                            sha1((Object) readContents(f)))) {
                        return;

                    }
                }
            }

            try {
                newfile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(newfile, (Object) readContents(f));
        } else {
            assert (newfile.isFile());

            // somewhere inside here to check the identical function
            // bool isIdentical() .///// uses sha1 ....

            if (!isIdentical(f, newfile)) {
                writeContents(newfile, (Object) readContents(f));

            }
            if (curr.getBlobmap() != null) {
                // check if blobmap contains the name or not
                if (curr.getBlobmap().containsKey(name)) {
                    if ((curr.getBlobmap().get(name)).equals(
                            sha1((Object) readContents(newfile)))) {
                        newfile.delete();

                    }
                }
            }
        }

    }

    private static boolean isEmptyDir(File dir) {
        String[] files = dir.list();
        return files != null && files.length == 0;
    }


    //Third parameter 'branchName' will be used in Merge Commits..
    public static void commit(String message, boolean b, String branchName) {
        //if the index folder is empty.... abort ...
        if (isEmptyDir(ADDITION_DIR) && isEmptyDir(REMOVAL_DIR)) {
            System.out.println("No changes added to the commit.");
            System.exit(0);

        }

        //take out all the files names from the staging area...
        String[] filenames = ADDITION_DIR.list();

        Commit prev = Commit.fromFile(HEAD);

        Commit newCommit;


        if (b) {
            newCommit = new Commit(message, getCurrentBranchCommitId(),
                    getBranchCommitId(branchName), prev);


        } else {

            newCommit = new Commit(message, getCurrentBranchCommitId(), prev);
        }


        assert filenames != null;
        for (String filename : filenames) {
            File f = join(ADDITION_DIR, filename);
            byte[] contents = readContents(f);
            String id = sha1((Object) contents);
            newCommit.getBlobmap().put(filename, id);

            //save the blob in objects folder

            File fblob = join(OBJ_DIR, id);
            if (!fblob.exists()) {
                try {
                    fblob.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                writeContents(fblob, (Object) contents);


            } else {
                assert (fblob.isFile());

            }

        }

        //Now stage for removal...
        //take out all the files names from the removal area...
        String[] filestobeRemoved = REMOVAL_DIR.list();
        for (String file : filestobeRemoved) {
            if (!newCommit.getBlobmap().containsKey(file)) {
                System.out.println("should have been in the previous commit....");
                System.exit(0);

            } else {
                newCommit.getBlobmap().remove(file);

            }
        }


        newCommit.saveCommmit();
        stagingAreaClearer(ADDITION_DIR);
        stagingAreaClearer(REMOVAL_DIR);


    }

    public static void checkout(String filename) {
        File f = join(CWD, filename);

        //unrap the head commit
        Commit curr = Commit.fromFile(HEAD);
        //check if head commit's blob contains filename
        if (!curr.getBlobmap().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File blob = join(OBJ_DIR, curr.getBlobmap().get(filename));

            writeContents(f, (Object) readContents(blob));


        } else {

            File blob = join(OBJ_DIR, curr.getBlobmap().get(filename));
            writeContents(f, (Object) readContents(blob));


        }


    }


    public static void checkout(String commitId, String filename) {
        File f = join(CWD, filename);

//        File commit = join(COMMITS_DIR, commitId);
        String commitIDFinal = resolveCommitId(commitId);
        File commit = join(COMMITS_DIR, commitIDFinal);
        if (!commit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);

        }
        //unrap the given commit id
        Commit c = Commit.fromFile(commitIDFinal);
        //check if head commit's blob contains filename
        if (!c.getBlobmap().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File blob = join(OBJ_DIR, c.getBlobmap().get(filename));
        writeContents(f, (Object) readContents(blob));


    }


//    //Checks if the file is currently being tracked by the given branch or not...
//    private static boolean istracked(String filename, File branch) {
//        Commit head = Commit.fromFile(branch);
//        return head.getBlobmap().containsKey(filename);
//    }

    private static void stagingAreaClearer(File file) {
        String[] filenames = file.list();
        for (String filename : filenames) {
            File f = join(file, filename);
            f.delete();

        }

    }

    //Here id could be a branch or a commit id...
    private static void checkoutInternals(String id) {
        //get the current commit
        Commit curr = Commit.fromFile(HEAD);
        Commit c;  //variable to store commit at the head of the given branch


        //check if it is a branch or a commit id...
        if (join(REFS, id).exists()) {
            c = Commit.fromFileB(id);  // id is actually a branch name here

        } else {
            c = Commit.fromFile(id);

        }
        //get the commit at the head of the given branch

        //simple case : just take out all the files from the blob
        //and put them in the working directory, overwriting the
        // versions of the files that are already there if they exist.

        if (c.getBlobmap() != null) {
            for (String filename : c.getBlobmap().keySet()) {

                File f = join(CWD, filename);
                //working file is untracked in the current branch (also check
                //if current branch is the initial commit
                if (curr.getBlobmap() == null
                        || (f.exists() && !curr.getBlobmap().containsKey(filename))) {
                    System.out.println("There is an untracked file in the way; delete it, or "
                            + "add and commit it first.");
                    System.exit(0);

                }
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                File blob = join(OBJ_DIR, c.getBlobmap().get(filename));
                writeContents(f, (Object) readContents(blob));


            }

        }


        //Any files that are tracked in the current branch but are not present
        // in the checked-out branch are deleted.
        if (curr.getBlobmap() != null) {
            for (String filename : curr.getBlobmap().keySet()) {
                File f = join(CWD, filename);

                if (f.exists()) {
                    if (c.getBlobmap() == null) {
                        restrictedDelete(f);

                    } else if (!c.getBlobmap().containsKey(filename)) {
                        restrictedDelete(f);

                    }

                }

            }

        }

        //clear the staging area...
        stagingAreaClearer(ADDITION_DIR);
        stagingAreaClearer(REMOVAL_DIR);


    }

    public static void checkoutB(String branch) {
        if (!join(REFS, branch).exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);

        } else if (currentBranchName().equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        checkoutInternals(branch);

        //finally the given branch will now be considered the current branch (HEAD).
        writeContents(HEAD, branch);


    }


    public static void log() {
        String commitId = getCurrentBranchCommitId();
        Commit c = Commit.fromFile(HEAD);
        while (c.getBlobmap() != null) {

            if (c.getParent1() != null && c.getParent2() != null) {
                String parent1 = c.getParent1();
                String parent2 = c.getParent2();

                Formatter f = new Formatter();
                f.format("===%n");
                f.format("commit %s%n", commitId);
                f.format("Merge: %s %s%n", parent1.substring(0, 7), parent2.substring(0, 7));
                f.format("Date: %s%n", c.getTimestamp());
                f.format("%s%n", c.getMessage());
                System.out.println(f);
                f.close();


                //follow the parent1
                commitId = c.getParent1();
                c = Commit.fromFile(commitId);


            } else {
                Formatter f = new Formatter();
                f.format("===%n");
                f.format("commit %s%n", commitId);
                f.format("Date: %s%n", c.getTimestamp());
                f.format("%s%n", c.getMessage());
                System.out.println(f);
                f.close();


                //follow the parent1
                commitId = c.getParent1();
                c = Commit.fromFile(commitId);

            }

        }

        //this has to be the head
        Formatter f = new Formatter();
        f.format("===%n");
        f.format("commit %s%n", commitId);
        f.format("Date: %s%n", c.getTimestamp());
        f.format("%s%n", c.getMessage());
        System.out.println(f);
//            System.out.println();
        f.close();



    }

    public static void globalLog() {
        List<String> commitIds = plainFilenamesIn(COMMITS_DIR);

        for (String id : commitIds) {
            Commit c = Commit.fromFile(id);

            if (c.getParent1() != null && c.getParent2() != null) {
                String parent1 = c.getParent1();
                String parent2 = c.getParent2();

                Formatter f = new Formatter();
                f.format("===%n");
                f.format("commit %s%n", id);
                f.format("Merge: %s %s%n", parent1.substring(0, 7), parent2.substring(0, 7));
                f.format("Date: %s%n", c.getTimestamp());
                f.format("%s%n", c.getMessage());
                System.out.println(f);
                f.close();

            } else {
                Formatter f = new Formatter();
                f.format("===%n");
                f.format("commit %s%n", id);
                f.format("Date: %s%n", c.getTimestamp());
                f.format("%s%n", c.getMessage());
                System.out.println(f);
                f.close();

            }


        }

//        System.out.println();

    }

    public static void find(String message) {

        List<String> commitIds = plainFilenamesIn(COMMITS_DIR);
        boolean found = false;

        for (String id : commitIds) {
            Commit c = Commit.fromFile(id);
            if (c.getMessage().equals(message)) {
                System.out.println(id);
                found = true;

            }


        }

        if (!found) {
            System.out.println("Found no commit with that message.");

        }
    }


    private static void printBranches() {
        String currBranch = currentBranchName();
        Formatter f = new Formatter();
        f.format("=== Branches ===%n");
        List<String> filenames = plainFilenamesIn(REFS);
        for (String name : filenames) {
            if (currBranch.equals(name)) {
                f.format("*%s%n", name);
            } else {
                f.format("%s%n", name);

            }

        }
        System.out.println(f);
        f.close();
    }

    private static void printStagedFiles() {
        Formatter f = new Formatter();
        f.format("=== Staged Files ===%n");
        List<String> filenames = plainFilenamesIn(ADDITION_DIR);
        for (String name : filenames) {
            f.format("%s%n", name);
        }
        System.out.println(f);
        f.close();

    }

    private static void printRemovedFiles() {
        Formatter f = new Formatter();
        f.format("=== Removed Files ===%n");
        List<String> filenames = plainFilenamesIn(REMOVAL_DIR);
        for (String name : filenames) {
            f.format("%s%n", name);
        }
        System.out.println(f);
        f.close();

    }

    //Checks if given File f is identical in the given commit ...
    private static boolean isIdentical(File f, String name, Commit commit) {
        return sha1((Object) readContents(f)).equals(commit.getBlobmap().get(name));


    }


    private static boolean isExist(File f) {
        return f.exists();
    }

    private static void printModifiedFilesandUntrackedFiles() {
        Formatter f1 = new Formatter();
        Commit c = Commit.fromFile(HEAD);  //curent -commit
        List<String> filesModifiedNotStaged = new ArrayList<>(); //to store filenames..de

        f1.format("=== Modifications Not Staged For Commit ===%n");
        System.out.print(f1);
        f1.close();


        List<String> filenames = plainFilenamesIn(CWD);
        List<String> filesStagedAddition = plainFilenamesIn(ADDITION_DIR);

        //Set of files which are tracked in the current commit,
        // changed in the working directory, but not staged; or
        //Not staged for removal, but tracked in the current commit
        // and deleted from the working directory.
        //also check if c is the initial commit or not...
        if (c.getBlobmap() != null) {
            for (String name : c.getBlobmap().keySet()) {

                File f = join(CWD, name);
                File fileStagedForAdd = join(ADDITION_DIR, name);
                File fileStagedForRemoval = join(REMOVAL_DIR, name);
                if (f.exists()) {
                    if (!isIdentical(f, name, c) && !isExist(fileStagedForAdd)) {
//                    f1.format("%s (modified)%n", name);
                        filesModifiedNotStaged.add(String.format("%s (modified)", name));

                    }

                } else {
                    if (!isExist(fileStagedForRemoval)) {
                        filesModifiedNotStaged.add(String.format("%s (deleted)", name));

                    }

                }
            }
        }

        //Set of files staged for addition
        for (String name : filesStagedAddition) {

            File f = join(CWD, name);
            if (f.exists()) {
                if (!isIdentical(f, join(ADDITION_DIR, name))) {
//                    f1.format("%s (modified)%n", name);
                    filesModifiedNotStaged.add(String.format("%s (modified)", name));

                }

            } else {
                filesModifiedNotStaged.add(String.format("%s (deleted)", name));

            }
        }

        //sort the filesnames and print them..
        Collections.sort(filesModifiedNotStaged);
        for (String name : filesModifiedNotStaged) {
            System.out.println(name);

        }
        System.out.println();


        //Final category ("Untracked files")
        printUntrackedFiles(c, filenames);

    }

    private static void printUntrackedFiles(Commit c, List<String> filenames) {
        Formatter f1 = new Formatter();
        f1.format("=== Untracked Files ===%n");
        for (String name : filenames) {

            if (c.getBlobmap() != null) {
                if ((!isExist(join(ADDITION_DIR, name)) && !c.getBlobmap().containsKey(name))
                        || isExist(join(REMOVAL_DIR, name))) {
                    f1.format("%s%n", name);
                }

            }

        }
        System.out.println(f1);
        f1.close();

    }

    public static void status() {
        printBranches();
        printStagedFiles();
        printRemovedFiles();
        printModifiedFilesandUntrackedFiles();
    }

    private static void deleteFile(File file, String filename) {
        File f = join(file, filename);
        //now check if its in the staging area or not
        if (f.exists() && f.isFile()) {
            //unstage the file
            f.delete();

        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);

        }

    }

    public static void rm(String filename) {
        //check if the file is currently being tracked by the current commit or not...
        Commit head = Commit.fromFile(HEAD);

        if (head.getBlobmap() != null) {
            if (!head.getBlobmap().containsKey(filename)) {
                //means its not currently being tracked by the current commit.

                deleteFile(ADDITION_DIR, filename);


            } else {

                File f = join(ADDITION_DIR, filename);
                if (f.exists() && f.isFile()) {
                    //unstage the file
                    f.delete();
                }

                //stage it for removal
                f = join(REMOVAL_DIR, filename);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //also delete the file from the CWD if it exists...
                if (join(CWD, filename).exists()) {
                    restrictedDelete(join(CWD, filename));
                }

            }

        } else {

            deleteFile(ADDITION_DIR, filename);

        }

    }


    public static void branch(String name) {
//        if (!readContentsAsString(HEAD).equals("master")) {
//            System.out.println("should be running with a default branch called “master”.");
//            System.exit(0);
//        }
        File newBranch = join(REFS, name);

        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);

        }

        try {
            newBranch.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeContents(newBranch, (Object) readContents(join(REFS, currentBranchName())));


    }

    public static void rmbranch(String name) {

        File f = join(REFS, name);
        if (!f.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);

        } else if (currentBranchName().equals(name)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        f.delete();

    }

    public static void reset(String id) {
//        String commitIDFinal = resolveCommitId(commitId);
        if (!join(COMMITS_DIR, resolveCommitId(id)).exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);

        }
        checkoutInternals(id);

        // Also moves the current branch’s head to that commit node.
        writeContents(join(REFS, currentBranchName()), id);

    }


    private static void initialSecurityMerge(String branchName) {
        if (!isEmptyDir(ADDITION_DIR) || !isEmptyDir(REMOVAL_DIR)) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        } else if (!join(REFS, branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);

        } else if (currentBranchName().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }


        // Get the commit of the branch we are merging in
        Commit givenCommit = fromFileB(branchName);

        Set<String> untracked = untrackedFiles();   // Get all untracked files in the CWD

        if (givenCommit.getBlobmap() != null) {
            for (String filename : givenCommit.getBlobmap().keySet()) {
                if (untracked.contains(filename)) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }

    }


    public static void checkoutAndStageFile(String branchName, String filename) {
        File f = join(CWD, filename);
        checkout(getBranchCommitId(branchName), filename);
        add(filename, f);

    }

    public static void handleMergeConflict(String branchName, String filename) {
        File file = join(CWD, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        String contents =
                "<<<<<<< HEAD\n"
                        + getFileContentsFromBranch(currentBranchName(), filename)
                        + "=======\n"
                        + getFileContentsFromBranch(branchName, filename)
                        + ">>>>>>>\n";

        writeContents(file, contents);
        add(filename, file);


    }


    private static boolean filesInAllThree(Set<String> filenames, String branchName) {
        boolean b = false;
        for (String filename : filenames) {
            if (isModified(branchName, filename) && !isModified(currentBranchName(), filename)) {
                checkoutAndStageFile(branchName, filename);


            } else if (isModified(branchName, filename) && isModified(currentBranchName(),
                    filename)) {
                if (hasConflictingChanges(branchName, currentBranchName(), filename)) {

                    //Merge conflict !!!!
                    b = true;
                    handleMergeConflict(branchName, filename);

                }


            }

        }
        return b;


    }

    private static boolean filesInHeadAndSplitOnly(Set<String> filenames, String branchName) {
        boolean b = false;
        for (String filename : filenames) {
            if (isModified(currentBranchName(), filename)) {
                b = true;
                handleMergeConflict(branchName, filename);

            } else {
                // should be removed (and untracked)
                rm(filename);


            }

        }
        return b;
    }

    private static boolean filesInOtherAndSplitOnly(Set<String> filenames, String branchName) {
        boolean b = false;
        for (String filename : filenames) {
            if (isModified(branchName, filename) && isModified(currentBranchName(), filename)) {
                b = true;
                handleMergeConflict(branchName, filename);

            }

        }
        return b;

    }

    private static Set<String> untrackedFiles() {
        Set<String> untracked = new HashSet<>();
        Commit c = Commit.fromFile(HEAD);  //curent -commit
        List<String> filenames = plainFilenamesIn(CWD);
        if (c.getBlobmap() != null) {
            for (String name : filenames) {

                if ((!isExist(join(ADDITION_DIR, name)) && !c.getBlobmap().containsKey(name))
                        || isExist(join(REMOVAL_DIR, name))) {
                    untracked.add(name);
                }

            }

        }

        return untracked;

    }


    private static void filesInOtherOnly(Set<String> filenames, String branchName) {
        for (String filename : filenames) {
            checkoutAndStageFile(branchName, filename);
        }
    }


    private static boolean filesInHeadAndOtherOnly(Set<String> filenames, String branchName) {
        boolean b = false;
        for (String filename : filenames) {
            if (hasConflictingChanges(branchName, currentBranchName(), filename)) {
                b = true;
                handleMergeConflict(branchName, filename);

            }

        }
        return b;

    }


    public static void merge(String branchName) {
        initialSecurityMerge(branchName);
        Merge.fillUpMap(currentBranchName());
        //find the splitPoint
        String splitPointId = Merge.splitPoint(branchName);

        //If the split point is the same commit as the given branch
        if (splitPointId.equals(getBranchCommitId(branchName))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);

        } else if (splitPointId.equals(getCurrentBranchCommitId())) {
            checkoutB(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);

        } else {


            boolean b1 = filesInAllThree(FileSets.files(true, true, true, splitPointId,
                            branchName),
                    branchName);
            boolean b2 = filesInHeadAndSplitOnly(FileSets.files(true, true, false, splitPointId,
                            branchName),
                    branchName);
            boolean b3 = filesInOtherAndSplitOnly(FileSets.files(false, true, true, splitPointId,
                            branchName),
                    branchName);
            filesInOtherOnly(FileSets.files(false, false, true, splitPointId, branchName),
                    branchName);
            boolean b4 = filesInHeadAndOtherOnly(FileSets.files(true, false, true, splitPointId,
                            branchName),
                    branchName);

            if (b1 || b2 || b3 || b4) {

                String message = "Merged " + branchName + " into " + currentBranchName() + ".";
                commit(message, true, branchName);


                System.out.println("Encountered a merge conflict.");


            } else {

                String message = "Merged " + branchName + " into " + currentBranchName() + ".";
                commit(message, true, branchName);


            }


        }


    }


}



