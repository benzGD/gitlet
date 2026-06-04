package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
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
    public static final File HEAD = join(COMMITS_DIR, "HEAD");

    /** File pointing to the objects/refs folder...*/
    public static File REFS = join(OBJ_DIR, "refs");

    /** Variable to store the master branch.
     * branch is just a pointer to a commit, nothing else...*/
    public static File master = join(REFS, "master");


    /** Variable to store the HEAD pointer. */
//    public static Commit HEAD;

    //use a string instead for HEAD and master variable????

//    public static String HEAD;

    // use a treemap datastructure from string to string and store it in somewhere????
    public static Map<String, String> map;

    /* TODO: fill in the rest of this class. */
      public static void setupRepository() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
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
              master.createNewFile();
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
          return  s1.equals(s2);
    }


   /* this function is incomplete ....
   * still have to implement
   * If the current working version of the file is identical to the version
   * in the current commit, do not stage it to be added, and remove it from
   * the staging area if it is already there */
    public static void add(String name, File f) {
        File newfile = join(ADDITION_DIR, name);
//        System.out.println(HEAD);
        Commit curr = Commit.fromFile(HEAD); //fix this

        if (!newfile.exists()) {
            //first time adding or ....
            if (curr.blobmap != null) {
                // check if blobmap contains the name or not
                if (curr.blobmap.containsKey(name)) {
                    if ( (curr.blobmap.get(name)).equals(sha1((Object) readContents(f))) ) {
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
            assert(newfile.isFile());

          // somewhere inside here to check the identical function
        // bool isIdentical() .///// uses sha1 ....

            if (!isIdentical(f, newfile)) {
                writeContents(newfile, (Object) readContents(f));

            }
            if (curr.blobmap != null) {
                // check if blobmap contains the name or not
                if (curr.blobmap.containsKey(name)) {
                    if ( (curr.blobmap.get(name)).equals(sha1((Object) readContents( newfile)))) {
                        newfile.delete();

                    }
                }
            }
        }

    }

    private static boolean isEmptyDir(File dir) {
        String[] files = dir.list();
        if (files != null && files.length == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void commit(String message){
        //if the index folder is empty.... abort ...
        if (isEmptyDir(ADDITION_DIR)) {
            System.out.println("No changes added to the commit.");
            System.exit(0);

        }

        //take out all the files names from the staging area...
        String[] filenames = ADDITION_DIR.list();

        Commit prev = Commit.fromFile(HEAD);
//        String id = readContentsAsString(HEAD);

        Commit new_commit = new Commit(message,  readContentsAsString(join(REFS, readContentsAsString(HEAD) )), prev);

        assert filenames != null;
        for (String filename : filenames) {
                File f = join(ADDITION_DIR, filename);
                byte[] contents = readContents(f);
                String id = sha1((Object) contents);
                new_commit.blobmap.put(filename, id);

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
        String[] files_to_be_removed = REMOVAL_DIR.list();
        for (String file : files_to_be_removed) {
            if (!new_commit.blobmap.containsKey(file)) {
                System.out.println("should have been in the previous commit....");
                System.exit(0);

            } else {
                new_commit.blobmap.remove(file);

            }
        }


        new_commit.saveCommmit();
        for (String filename : filenames ) {
            File f = join(ADDITION_DIR, filename);
            f.delete();

        }

    }

    public static void checkout(String filename) {
        File f = join(CWD, filename);

        //unrap the head commit
        Commit curr = Commit.fromFile(HEAD);
        //check if head commit's blob contains filename
        if (!curr.blobmap.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File blob = join(OBJ_DIR, curr.blobmap.get(filename));

            writeContents(f, (Object) readContents(blob));


        } else {

            File blob = join(OBJ_DIR, curr.blobmap.get(filename));
            writeContents(f, (Object) readContents(blob));



        }


    }


    public static void checkout(String commit_id, String filename) {
        File f = join(CWD, filename);

        File commit = join(COMMITS_DIR, commit_id);
        if (!commit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);

        }
        //unrap the given commit id
        Commit c = Commit.fromFile(commit_id);
        //check if head commit's blob contains filename
        if (!c.blobmap.containsKey(filename)) {
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
            File blob = join(OBJ_DIR, c.blobmap.get(filename));
            writeContents(f, (Object) readContents(blob));


        }



//    //Checks if the file is currently being tracked by the given branch or not...
//    private static boolean istracked(String filename, File branch) {
//        Commit head = Commit.fromFile(branch);
//        return head.blobmap.containsKey(filename);
//    }

    private static void staging_area_clearer(File file) {
        String[] filenames = file.list();
        for (String filename : filenames ) {
            File f = join(file, filename);
            f.delete();

        }

    }

    //Here id could be a branch or a commit id...
    private static void checkout_internals(String id) {
        //get the current commit
        Commit curr = Commit.fromFile(HEAD);
        Commit c;  //variable to store commit


        //check if its a branch or a commit id...
        if (join(REFS, id).exists()) {
             c = Commit.fromFile_B(join(REFS, id));

        } else {
             c = Commit.fromFile(id);

        }
        //get the commit at the head of the given branch

        //simple case : just take out all the files from the blob
        //and put them in the working directory, overwriting the
        // versions of the files that are already there if they exist.
        for (String filename : c.blobmap.keySet() ) {

            File f = join(CWD, filename);
            //working file is untracked in the current branch
            if (f.exists() && !curr.blobmap.containsKey(filename)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);

            }
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            File blob = join(OBJ_DIR, c.blobmap.get(filename));
            writeContents(f, (Object) readContents(blob));


        }


        //Any files that are tracked in the current branch but are not present
        // in the checked-out branch are deleted.
        for (String filename : curr.blobmap.keySet() ) {
            File f = join(CWD, filename);

            if (f.exists() &&  !c.blobmap.containsKey(filename)) {
                restrictedDelete(f);

            }

        }

        //clear the staging area...
        staging_area_clearer(ADDITION_DIR);
        staging_area_clearer(REMOVAL_DIR);



    }

    public static void checkout_b(String branch) {
        if (!join(REFS, branch).exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);

        } else if (readContentsAsString(HEAD).equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);

        }

        checkout_internals(branch);

        //finally the given branch will now be considered the current branch (HEAD).
        writeContents(HEAD, branch);



    }





    public static void log() {
        String commit_id = readContentsAsString(join(REFS, readContentsAsString(HEAD) )); //update- this  id
        Commit c = Commit.fromFile(HEAD);
        while (c.blobmap != null){
            Formatter f = new Formatter();
            f.format("===%n");
            f.format("commit %s%n", commit_id);
            f.format("Date: %s%n", c.getTimestamp());
            f.format("%s%n", c.getMessage());
            System.out.println(f);
            f.close();


            //follow the parent1
            commit_id = c.getParent1();
            c = Commit.fromFile(commit_id);

        }

        //this has to be the head
        if (c.blobmap == null) {
            Formatter f = new Formatter();
            f.format("===%n");
            f.format("commit %s%n", commit_id);
            f.format("Date: %s%n", c.getTimestamp());
            f.format("%s%n", c.getMessage());
            System.out.println(f);
            System.out.println();
            f.close();

        }

    }

    public static void rm(String filename) {
        //check if the file is currently being tracked by the current commit or not...
        Commit head = Commit.fromFile(HEAD);
        if (!head.blobmap.containsKey(filename)) {
            //means its not currently being tracked by the current commit.

            //now check if its in the staging area or not
            File f = join(ADDITION_DIR, filename);
            if (f.exists() && f.isFile()) {
                //unstage the file
                f.delete();

            } else {
                System.out.println("No reason to remove the file.");
                System.exit(0);

            }


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

    }


    public static void branch(String name) {
//        if (!readContentsAsString(HEAD).equals("master")) {
//            System.out.println("should be running with a default branch called “master”.");
//            System.exit(0);
//        }
        File new_branch = join(REFS, name);

        if (new_branch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);

        }

        try {
            new_branch.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeContents(new_branch, (Object) readContents(join(REFS, "master")));


    }

    public static void rmbranch(String name) {

        File f = join(REFS, name);
        if (!f.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);

        } else if (readContentsAsString(HEAD).equals(name)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        f.delete();

    }

    public static void reset(String id) {
        if (!join(COMMITS_DIR, id).exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);

        }
        checkout_internals(id);

        // Also moves the current branch’s head to that commit node.
        writeContents(join(REFS, readContentsAsString(HEAD )), id);

    }










    }



