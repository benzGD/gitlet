package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author gd
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The commmit directory */
    public static final File COMMIT_DIR = join(Repository.GITLET_DIR, "commits");

    /** The message of this Commit. */
    private String message;

    /** The timestamp of this Commit. */
    private Date timestamp;

    /** The parent Commit of this Commit. */
    private String parent;


    /* TODO: something to contain filenames and references to their blobs */





    /* TODO: fill in the rest of this class. */

    public Commit(String message, String parent) {


        this.message = message;
        this.parent = parent;

        if (parent == null) {

            this.timestamp = new  Date(0);

        } else  {
            this.timestamp = new Date();
        }

    }


    /* Saves the commit to the COMMITT_dir*/
    /*in what name to save it????????/*/
    public void saveCommmit()  {






    }









}
