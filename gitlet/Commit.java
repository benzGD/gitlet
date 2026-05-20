package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.*;

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

    /** The first parent Commit of this Commit. */
    private String parent1;

    /** The second parent Commit of this Commit. */
    private String parent2;


    /* TODO: something to contain filenames and references to their blobs */
    public Map<String, String> blobmap;



    /* TODO: fill in the rest of this class. */

    public Commit(String message, String parent, Commit other) {


        this.message = message;
        this.parent1 = parent;
//        this.blobmap = new TreeMap<>();

        if (parent == null) {
            this.timestamp = new  Date(0);
            this.blobmap = null;

        } else  {
            this.timestamp = new Date();
            if (other.blobmap == null) {
                this.blobmap = new TreeMap<>();

            } else {
                this.blobmap = new TreeMap<>(other.blobmap);
            }

        }

    }

    public static Commit fromFile(File file) {
        String name = readContentsAsString(file);
        File in = new File(Repository.COMMITS_DIR, name);
        return readObject(in, Commit.class);
    }

    public static Commit fromFile(String commit_id) {
        File in = new File(Repository.COMMITS_DIR, commit_id);
        return readObject(in, Commit.class);
    }



    /* Saves the commit to the COMMITT_dir*/
    /*in what name to save it????????/*/
    public void saveCommmit()  {
        String id = sha1((Object) serialize(this));
        writeContents(Repository.HEAD, id);
        File f = new File(Repository.COMMITS_DIR, id);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        //may not be correct!!!!!!!!!!!!! FIX THIS
        writeObject(f, this);


    }

    public String getMessage(){
        return  this.message;
    }

    // --- FIXED DATE PART ---
    public String getTimestamp(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return formatter.format(this.timestamp);
    }

    public String getParent1(){
        return  this.parent1;
    }

    public String getParent2(){
        return  this.parent2;
    }











}
