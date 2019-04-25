package org.jkcsoft.codetests.karat;

import java.util.*;

/**
 * @author Jim Coles
 */

// You are in charge of a display advertising program. Your ads are displayed on websites all over the internet. You have some CSV input data that counts how many times you showed an ad on each individual domain. Every line consists of a count and a domain name. It looks like this:

// counts = [ "900,google.com",
//      "60,mail.yahoo.com",
//      "10,mobile.sports.yahoo.com",
//      "40,sports.yahoo.com",
//      "300,yahoo.com",
//      "10,stackoverflow.com",
//      "2,en.wikipedia.org",
//      "1,es.wikipedia.org",
//      "1,mobile.sports" ]

// Write a function that takes this input as a parameter and returns a data structure containing the number of hits that were recorded on each domain AND each domain under it. For example, an impression on "mail.yahoo.com" counts for "mail.yahoo.com", "yahoo.com", and "com". (Subdomains are added to the left of their parent domain. So "mail" and "mail.yahoo" are not valid domains. Note that "mobile.sports" appears as a separate domain as the last item of the input.)

// Sample output (in any order/format):

// getTotalsByDomain(counts)
// 1320    com
//  900    google.com
//  410    yahoo.com
//   60    mail.yahoo.com
//   10    mobile.sports.yahoo.com
//   50    sports.yahoo.com
//   10    stackoverflow.com
//    3    org
//    3    wikipedia.org
//    2    en.wikipedia.org
//    1    es.wikipedia.org
//    1    mobile.sports
//    1    sports
public class KaratTest {
    public static void main(String[] args) {
        String[] counts = {
            "900,google.com",
            "60,mail.yahoo.com",
            "10,mobile.sports.yahoo.com",
            "40,sports.yahoo.com",
            "300,yahoo.com",
            "10,stackoverflow.com",
            "2,en.wikipedia.org",
            "1,es.wikipedia.org",
            "1,mobile.sports"
        };

        KaratTest sol = new KaratTest();
        List<Result> locResults = sol.getTotalsByDomain(counts);
        locResults.sort((o1, o2) -> -Integer.compare(o1.total, o2.total));
        for (Result res : locResults) {
            log(res.toString());
        }

    }

    private static void log(String msg) {
        System.out.println(msg);
    }
    private List<Result> results = new LinkedList<>();

    public KaratTest() {

    }

    public List<Result> getTotalsByDomain(String[] counts) {
        TreeNode root = new TreeNode(null, "root", 0);
        for (String count : counts) {
            int idxComma = count.indexOf(",");
            String strCount = count.substring(0, idxComma);
            String strFullName = count.substring(idxComma + 1, count.length());
            log("count parse: " + strCount);
            int numcount = Integer.parseInt(strCount);
            log("name parse: " + strFullName);
            String names[] = strFullName.split("\\.");

//      log(names[0]);
            // build tree
            TreeNode activeNode = root;
            for (int idx = names.length - 1; idx >= 0; idx--) {
                activeNode = activeNode.addChild(names[idx], numcount, idx == 0);
            }
        }
        // for each leaf node print full name and count
        walkTreeRec(root);
        return results;
    }

    // Statics

    private void walkTreeRec(TreeNode node) {
        if (!node.isRoot()) {
            Result result = new Result();
            result.fullName = node.getFullPath();
            result.total = node.getTotalCount();
            results.add(result);
        }
        if (node.isLeaf()) {
        }
        else {
            for (TreeNode child : node.getChildren()) {
                walkTreeRec(child);
            }
        }
    }

    public static class Result {
        public int total;
        public String fullName;

        public Result() {
        }

        public String toString() {
            return String.format("%6d   %s", total, fullName);
        }
    }

    public static class TreeNode {
        private TreeNode parent;
        private int nodeCount;
        private String name;
//        private List<TreeNode> childNodes = new LinkedList<>();
        private Map<String, TreeNode> childMap = new TreeMap<>();

        public TreeNode(TreeNode parent, String name, int nodeCount) {
            this.parent = parent;
            this.name = name;
            this.nodeCount = nodeCount;
        }

        public TreeNode addChild(String name, int count, boolean isLastNode) {
            TreeNode child = childMap.get(name);
            if (child != null) {
                if (isLastNode)
                    child.setNodeCount(count);
            }
            else {
                child = new TreeNode(this, name, (isLastNode ? count : 0));
//                childNodes.add(child);
                childMap.put(name, child);
                log("add child [" + child.getName() + "] to [" + this.getFullPath() + "]");
            }
            return child;
        }

        private void setNodeCount(int nodeCount) {
            this.nodeCount = nodeCount;
        }

        public String getName() {
            return name;
        }

        public Collection<TreeNode> getChildren() {
            return childMap.values();
        }

        public boolean isLeaf() {
            return childMap.size() == 0;
        }

        public boolean isRoot() {
            return parent == null;
        }

        public int getTotalCount() {
            int count = 0;
            if (parent == null) {
                if (isLeaf())
                    count = nodeCount;
            }
            else {
                count = nodeCount;
                for(TreeNode child : childMap.values()) {
                    count += child.getTotalCount();
                }
            }
            return count;
        }

        public String getFullPath() {
            if (parent == null) {
                return "";
            }
            else {
                return name + (!parent.isRoot() ? "." + parent.getFullPath() : "");
            }
        }

    }

}
