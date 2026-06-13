package gitlet;

import static gitlet.Repository.HEAD;
import static gitlet.Repository.REFS;
import static gitlet.Utils.join;
import static gitlet.Utils.readContentsAsString;

public class Branch {
    public static String getCurrentBranchCommitId() {
        return readContentsAsString(join(REFS, readContentsAsString(HEAD)));

    }

    public static String getBranchCommitId(String branchName) {
        return readContentsAsString(join(REFS, branchName));


    }

    public static String currentBranchName() {
        return readContentsAsString(HEAD);
    }




}
