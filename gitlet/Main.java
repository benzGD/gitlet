package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.setupRepository();
                break;
            case "add":
                //check if gitlet exists or not...
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an intialized Gitlet directory.");
                    System.exit(0);
                }
                if (args[1] == null) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                String filename = args[1];
                File f = new File(Repository.CWD, filename);
                if (!f.isFile()) {
                    System.out.println("File does not exist.");
                    System.exit(0);
                }
                Repository.add(filename, f);
                break;
            case "commit":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an intialized Gitlet directory.");
                    System.exit(0);
                }
                if (args[1] == null) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }

                String message = args[1];
                Repository.commit(message);

            // TODO: FILL THE REST IN
        }
    }
}
