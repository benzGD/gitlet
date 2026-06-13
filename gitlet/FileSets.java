package gitlet;

import java.util.HashSet;
import java.util.Set;

import static gitlet.Commit.*;

public class FileSets {

    static Set<String> files(
            boolean inHead,
            boolean inSplit,
            boolean inOther,
            String splitPointId,
            String branchName) {

        Set<String> head = getTrackedFileNamesHEAD();
        Set<String> other = getTrackedFileNamesB(branchName);
        Set<String> split = getTrackedFileNamesCommit(splitPointId);

        Set<String> all = new HashSet<>();
        all.addAll(head);
        all.addAll(other);
        all.addAll(split);

        Set<String> result = new HashSet<>();

        for (String file : all) {
            if (head.contains(file) == inHead
                    && split.contains(file) == inSplit
                    && other.contains(file) == inOther) {
                result.add(file);
            }
        }

        return result;
    }

}
