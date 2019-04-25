/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2019 through present.
 *
 * Licensed under the following license agreement:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.codetests.hackerrank.clouds;

import java.util.*;

/**
 * @author Jim Coles
 */
public class Solution {

    private static final Scanner scanner = new Scanner(System.in);
    // Complete the jumpingOnClouds function below.
    private static Map<Integer, Cloud> nodeMap;

    static int jumpingOnClouds(int[] c) {
        // populate one TreeNode per cloud by idx
        nodeMap = new HashMap();
        for (int idx = 0; idx < c.length; idx++) {
            nodeMap.put( idx, new Cloud(idx, c[idx], idx == c.length -1) );
        }
        // create DAG of valid paths, not nec. ending at the end
        addSafeOthers(nodeMap.get(0));
        // count valid paths to end
        List<CloudPath> possiblePaths = new LinkedList<>();
        CloudPath firstPath = new CloudPath();
        firstPath.getCloudPath().add(nodeMap.get(0));
        possiblePaths.add(firstPath);
        //
        addNextPathsToEndRec(possiblePaths, firstPath);
        //
        System.out.println("num poss paths: " + possiblePaths.size());
        int minPath = Integer.MAX_VALUE;
        for (CloudPath validPath : possiblePaths) {
            if (validPath.isValid())
                minPath = Math.min(minPath, validPath.getCloudPath().size() - 1);
        }
        return minPath;
    }

    /**
     * If active cloud has safe next nodes:
     *   For first next node:
     *      add to end of current path
     *      recursive call with that path
     *   For each 'next nodes' 2...n
     *      duplicate first path and add the next node to end
     *      recursive call with that dup'd path
     *
     * For first next safe path
     * @param allPossiblePaths all paths being explored.
     * @param currentPath the path (already in allPossiblePaths) with current node
     * @return
     */
    static void addNextPathsToEndRec(List<CloudPath> allPossiblePaths, CloudPath currentPath) {
        Cloud visitCloud = currentPath.getCloudPath().getLast();
        if (!visitCloud.isStop()) {
            CloudPath visitPathCopy = null;
            if (visitCloud.getSafeNextClouds().size() > 1)
                 visitPathCopy = currentPath.copy();
            if (visitCloud.getSafeNextClouds().size() > 0) {
                Iterator<Cloud> iterNextOnes = visitCloud.getSafeNextClouds().iterator();
                currentPath.getCloudPath().add(iterNextOnes.next());
                addNextPathsToEndRec(allPossiblePaths, currentPath);
                while (iterNextOnes.hasNext()) {
                    CloudPath dupPath = visitPathCopy.copy();
                    allPossiblePaths.add(dupPath);
                    dupPath.getCloudPath().add(iterNextOnes.next());
                    addNextPathsToEndRec(allPossiblePaths, dupPath);
                }
            }
            else
                currentPath.setInvalid();
        }
    }

    private static boolean isEnd(Cloud node) {
        return isEnd(node.idx);
    }

    private static boolean isEnd(int idx) {
        return idx == nodeMap.size() - 1;
    }

    static void addSafeOthers(Cloud from) {
        for (int idx = from.idx + 1;
             idx <= from.idx + 2 && idx < nodeMap.entrySet().size();
             idx++) {
            Cloud testNode = nodeMap.get(idx);
            from.addIfSafe(testNode);
            if (!testNode.visited && testNode.isCumulus()) {
                addSafeOthers(testNode);
            }
        }
        from.visited = true;
    }

    public static void main(String[] args) {
        testCloudHopper("simple 4", new int[]{0, 0, 1, 0, 0, 1, 0});
        testCloudHopper("2-paths 3", new int[]{0, 0, 0, 1, 0, 1, 0});
        testCloudHopper("none", new int[]{0, 1, 1, 0, 0, 0, 0});
    }

    private static void testCloudHopper(String name, int[] cloudSpec) {
        System.out.println("-- Test [" + name + "] --");
        System.out.println("min hops " + jumpingOnClouds(cloudSpec));
    }

    private static class CloudPath {

        private boolean isValid = true;
        private LinkedList<Cloud> cloudPath = new LinkedList<>();

        public boolean isValid() {
            return isValid;
        }

        public void setInvalid() {
            this.isValid = false;
        }

        public LinkedList<Cloud> getCloudPath() {
            return cloudPath;
        }

        public CloudPath copy() {
            CloudPath dupPath = new CloudPath();
            dupPath.getCloudPath().addAll(this.getCloudPath());
            return dupPath;
        }
    }

    private static class Cloud {
        private boolean visited = false;
        private int cloudType;
        private int idx;
        private List<Cloud> safeNextClouds = new LinkedList<>();
        private boolean isStop = false;

        public Cloud(int idx, int cloudType, boolean isStop) {
            this.idx = idx;
            this.cloudType = cloudType;
            this.isStop = isStop;
        }

        public Cloud(int idx, int cloudType) {
            this(idx, cloudType, false);
        }

        public void addIfSafe(Cloud testNode) {
            if (isSafe(testNode)) {
                safeNextClouds.add(testNode);
            }
        }

        private boolean isSafe(Cloud toNode) {
            return toNode.isCumulus() && Math.abs(this.idx - toNode.idx) <= 2;
        }

        public List<Cloud> getSafeNextClouds() {
            return safeNextClouds;
        }

        public boolean isCumulus() {
            return this.cloudType == 0;
        }

        public boolean isStop() {
            return isStop;
        }

        @Override
        public String toString() {
            return "Cloud {" +
                "idx=" + idx +
                ", cloudType=" + cloudType +
                ", safeNextNodes.size=" + safeNextClouds.size() +
                ", visited=" + visited +
                ", isStop=" + isStop +
                '}';
        }
    }

}
