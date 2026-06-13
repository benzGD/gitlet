package gitlet;

import java.io.File;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        String filename;
        String name;
        File f;
        switch (firstArg) {
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
                filename = args[1];
                f = validateFile(filename);
                Repository.add(filename, f);
                break;
            case "commit":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an intialized Gitlet directory.");
                    System.exit(0);
                }
                if (args[1] == null) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                String message = args[1];
                if (message.isEmpty()) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.commit(message, false, null);
                break;
            case "checkout":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an intialized Gitlet directory.");
                    System.exit(0);
                }
                if (args.length == 2) {
                    filename = args[1];
                    Repository.checkoutB(filename);

                } else if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);

                    }
                    filename = args[2];
                    Repository.checkout(filename);

                } else if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);

                    }
                    filename = args[3];
                    Repository.checkout(args[1], filename);

                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);

                }
                break;
            case "log":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an intialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.log();
                break;
            case "global-log":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an intialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.globalLog();
                break;
            case "find":
                name = args[1];
                initialSecurity(name);
                Repository.find(name);
                break;
            case "rm":
                initialSecurity(args[1]);
                filename = args[1];
                Repository.rm(filename);
                break;
            case "branch":
                name = args[1];
                initialSecurity(name);
                Repository.branch(name);
                break;
            case "rm-branch":
                name = args[1];
                initialSecurity(name);
                Repository.rmbranch(name);
                break;
            case "reset":
                name = args[1];
                initialSecurity(name);
                Repository.reset(name);
                break;
            case "status":
                name = args[0];
                initialSecurity(name);
                Repository.status();
                break;
            case "merge":
                name = args[1];
                initialSecurity(name);
                Repository.merge(name);
                break;



            default:
                System.out.println("No command with that name exists.");
                System.exit(0);

        }
    }

    public static File validateFile(String name) {
        File f = new File(Repository.CWD, name);
        if (!f.isFile()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        return f;
    }

    public static void initialSecurity(String s) {
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an intialized Gitlet directory.");
            System.exit(0);
        }
        if (s == null) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

    }


}
