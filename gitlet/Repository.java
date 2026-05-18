package gitlet;

import java.io.File;
import java.io.IOException;
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

    /* The commits directory. */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

    /*The objects directory. */
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");

    /*File pointing to the initial commit. */
    public static final File HEAD = join(COMMITS_DIR, "HEAD");



    /** Variable to store the master branch.
     * branch is just a pointer to a commit, nothing else...*/
    public static Commit master;


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
        COMMITS_DIR.mkdir();
        OBJ_DIR.mkdir();
          try {
              HEAD.createNewFile();
          } catch (IOException e) {
              throw new RuntimeException(e);
          }

          Commit head = new Commit("initial commit", null, null);
        //save it in the commits directory


//          String id_of_first_commit = sha1((Object) serialize(head));
//
//          File f = new File(COMMITS_DIR, HEAD);
//          if (!f.exists()) {
//              try {
//                  f.createNewFile();
//              } catch (IOException e) {
//                  throw new RuntimeException(e);
//              }
//
//          }
//          //may not be correct!!!!!!!!!!!!! FIX THIS
//          writeObject(f, head);

//          writeContents(HEAD, id_of_first_commit);

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
        File newfile = join(INDEX_DIR, name);
//        System.out.println(HEAD);
        Commit curr = Commit.fromFile(HEAD);

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
        if (isEmptyDir(INDEX_DIR)) {
            System.out.println("No changes added to the commit.");
            System.exit(0);

        }

        //take out all the files names from the staging area...
        String[] filenames = INDEX_DIR.list();

        Commit prev = Commit.fromFile(HEAD);
//        String id = readContentsAsString(HEAD);

        Commit new_commit = new Commit(message, readContentsAsString(HEAD), prev);

        assert filenames != null;
        for (String filename : filenames) {
                File f = join(INDEX_DIR, filename);
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

        new_commit.saveCommmit();
          //make  a commit object ....
        //copy the blobmap of the latest head commit to the
        //to the current commit and make the current commit as head

        //  If the current working version of the file is identical
        //  to the version in the current commit, do not stage it to be added,
        //  and remove it from the staging area if it is already there


        //save the commit object in the commits folder under its sha1 name..

        // remove all the files from the staging area
        for (String filename : filenames ) {
            File f = join(INDEX_DIR, filename);
            f.delete();

        }

    }

}
