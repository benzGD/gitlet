package gitlet;

import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.readContentsAsString;

public class Merge {
    // It's highly recommended to clear these before starting a new merge!
    static Map<String, Integer> distanceMap = new HashMap<>();
    static Map<String, Boolean> marked = new HashMap<>();
    static String splitPoint = "";

    // Returns the adjacent vertices to "node" id "v"
    static List<String> adj(String v) {
        Commit c = Commit.fromFile(v);
        List<String> vertices = new LinkedList<>();

        if (c.getParent1() != null) {
            vertices.add(c.getParent1());
        }
        if (c.getParent2() != null) {
            vertices.add(c.getParent2());
        }
        return vertices;
    }

    public static void fillUpMap(String branch) {
        // Clear maps to prevent contamination from previous commands
        distanceMap.clear();
        marked.clear();

        Queue<String> queue = new LinkedList<>();
        String id = readContentsAsString(join(Repository.REFS, branch));
        queue.offer(id);
        marked.put(id, true);
        distanceMap.put(id, 0);

        while (!queue.isEmpty()) {
            String v = queue.poll();
            for (String w : adj(v)) {
                if (!marked.getOrDefault(w, false)) {
                    queue.offer(w);
                    marked.put(w, true);
                    distanceMap.put(w, distanceMap.get(v) + 1);
                }
            }
        }
    }

    public static String splitPoint(String branch) {
        // IMPORTANT: Clear the marked map so the second BFS can actually traverse!
        marked.clear();
        splitPoint = ""; // Reset split point

        Queue<String> queue = new LinkedList<>();
        String id = readContentsAsString(join(Repository.REFS, branch));

        // CHECK 1: Is the starting node already the split point?
        if (distanceMap.containsKey(id)) {
            return id;
        }

        queue.offer(id);
        marked.put(id, true);

        while (!queue.isEmpty()) {
            String v = queue.poll();
            for (String w : adj(v)) {
                if (!marked.getOrDefault(w, false)) {
                    queue.offer(w);
                    marked.put(w, true);

                    // CHECK 2: Is this adjacent node in the first branch's history?
                    if (distanceMap.containsKey(w)) {
                        if (splitPoint.isEmpty()) {
                            splitPoint = w;
                        } else if (distanceMap.get(w) < distanceMap.get(splitPoint)) {
                            splitPoint = w;
                        }
                    }
                }
            }
        }

        return splitPoint;
    }
}
