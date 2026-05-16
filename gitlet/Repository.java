package gitlet;

import java.io.File;
import java.io.IOException;

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


    /** Variable to store the master branch.
     * branch is just a pointer to a commit, nothing else...*/
    public static Commit master;


    /** Variable to store the HEAD pointer. */
    public static Commit HEAD;


    /* TODO: fill in the rest of this class. */
      public static void setupRepository() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);

        }
        GITLET_DIR.mkdir();

        //make 2 folders index a.k.a staging area and commits
        File index = new File(GITLET_DIR, "index");
        index.mkdir();
        File commits = new File(GITLET_DIR, "commits");
        commits.mkdir();
        File obj = new File(GITLET_DIR, "objects");
        obj.mkdir();

        HEAD = new Commit("initial commit", null);

        //save it in the commits directory

          String filename = sha1((Object) serialize(HEAD));

          File f = new File(commits, filename);
          if (!f.exists()) {
              try {
                  f.createNewFile();
              } catch (IOException e) {
                  throw new RuntimeException(e);
              }

          }

          writeObject(f, HEAD);


    }





}
