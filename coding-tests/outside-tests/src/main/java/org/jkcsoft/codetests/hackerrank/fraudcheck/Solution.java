package org.jkcsoft.codetests.hackerrank.fraudcheck;

/**
 * @author Jim Coles
 */

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class Solution {

    private static Comparator<Debit> amountComp = Comparator.<Debit, Float>comparing(o -> o.amount);
    private static Comparator<Debit> theComp = amountComp.thenComparingInt(value -> value.id);

    // Complete the activityNotifications function below.
    // Complete the activityNotifications function below.
    static int activityNotifications(int[] expenditure, int trailingDays) {
        return activityNotificationsJucMaps(expenditure, trailingDays);
//        return activityNotificationsCustomTree(expenditure, trailingDays);
//        return activityNotificationsInsertionSort(expenditure, trailingDays);
    }

    /**
     * Sort by computing an integer associated with the sort-by value(s) and
     * placing the sorted object (reference) into the proper array slot. The holding
     * structure is an array of arrays. This is a kind of tree, but with better
     * random access time.
     */
    static int activityNotificationsRecurDecomp(int[] expenditure, int trailingDays) {
        int numNotifs = 0;

        ArrayList<Debit> sortArrayByAmt = new ArrayList<>(trailingDays);
        for (int idxExp = 0; idxExp < trailingDays; idxExp++) {
            Debit debit = newDebitObject(expenditure, idxExp);
            sortArrayByAmt.add(debit);
        }


        return numNotifs;
    }

    public static class DecompArray {
        private int max, min;
        private int numSlots;
        private DecompArray[] children;

    }

    public static class IntegralComparator {
        private int radix;
    }

    static int activityNotificationsInsertionSort(int[] expenditure, int trailingDays) {
        int numNotifs = 0;

        ArrayList<Debit> sortArrayByAmt = new ArrayList<>(trailingDays);
        for (int idxExp = 0; idxExp < trailingDays; idxExp++) {
            Debit debit = newDebitObject(expenditure, idxExp);
            sortArrayByAmt.add(debit);
        }


        return numNotifs;
    }

    static int activityNotificationsJucMaps(int[] expenditure, int trailingDays) {
        int numNotifs = 0;

        SortedSet<Debit> debitSet = new TreeSet<>(theComp);
        SortedMap<Float, Debit> debitSortedMap = new TreeMap();

        for (int idxExp = 0; idxExp < trailingDays; idxExp++) {
            Debit debit = newDebitObject(expenditure, idxExp);

            // How are objects added internally?
            debitSet.add(debit);
            debitSortedMap.put(debit.amount, debit);
        }

        for (int idxExp = 0; idxExp < trailingDays; idxExp++) {
            Debit debit = newDebitObject(expenditure, idxExp);

            // What is the cost of building a random access array?
            debitSet.iterator();
            debitSet.spliterator();
            debitSet.toArray();
            debitSortedMap.values().toArray();

            // What is the cost of finding and removing an object?
            debitSet.remove(debit);
            debitSortedMap.remove(debit.amount, debit);
        }


        return numNotifs;
    }

    static int activityNotificationsCustomTree(int[] expenditure, int trailingDays) {
        int numNotifs = 0;

        long markTime = System.currentTimeMillis();

//        Arrays.sort(expenditure);
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (int exp : expenditure) {
            max = Math.max(max, exp);
            min = Math.min(min, exp);
        }
        System.out.println("min "+ min +" max " + max);
        if (trailingDays <= expenditure.length) {
//            int[] trailingExps = new int[trailingDays];
//            System.arraycopy(expenditure, 0, trailingExps, 0, trailingDays);
//            Arrays.sort(trailingExps);
            EventTree<Debit> trailingDebits = new EventTree<>(theComp, Debit::getId, Debit::getAmount);
            for (int idxExp = 0; idxExp < trailingDays; idxExp++) {
                trailingDebits.shouldLog = shouldLog(expenditure, idxExp);
                trailingDebits.add(newDebitObject(expenditure, idxExp));
            }

//            trailingDebits.printTree();

            for(int idxEvalDay = trailingDays; idxEvalDay < expenditure.length; idxEvalDay++) {
                int expEvalDay = expenditure[idxEvalDay];
                boolean shouldLog = shouldLog(expenditure, idxEvalDay);
                trailingDebits.shouldLog = shouldLog;
                if (idxEvalDay > trailingDays) {
//                    trailingExps[0] = expenditure[idxEvalDay];
//                    Arrays.sort(trailingExps);
//                    insertionSortImperative(trailingExps);
                    trailingDebits.add(newDebitObject(expenditure, idxEvalDay - 1));
                    int idxRemDay = idxEvalDay - trailingDays - 1;
                    trailingDebits.removeNodeByValueId(idxRemDay);
                }
//                expLeftmost = expenditure[idxDayStart];
                // delete one value == previous leftmost (drops off the table); add current
                // using insertion sort
                float median = trailingDebits.getMedian();
                float maxAllowedExp = 2.0f * median;
                if (expEvalDay >= maxAllowedExp) {
                    System.out.println(String.format("warning: on day %d, debit of %d >= 2 x median %.1f = %.1f",
                                                     idxEvalDay, expEvalDay, median, maxAllowedExp));
                    numNotifs++;
                }

                if (shouldLog) {
                    int idxMark = idxEvalDay;
                    long nowTime = System.currentTimeMillis();
                    long delta = nowTime - markTime;
                    System.out.println(String.format("time delta [" + delta + "] progress %d num: %d",
                                                     idxEvalDay, numNotifs));
                    markTime = nowTime;
                }
            }
        }

        return  numNotifs;
    }

    private static boolean shouldLog(int[] expenditure, int idxEvalDay) {
        return expenditure.length <= 100 || idxEvalDay % (expenditure.length/100) == 0;
    }

    private static Debit newDebitObject(int[] expenditure, int idx) {
        return new Debit(idx, expenditure[idx]);
    }

    public static float getMedian(int[] values) {
        int nDebits = values.length;
        boolean isOddDays = nDebits % 2 != 0;
        int idxMedianRef = (nDebits - 1) / 2;
        float medianExp = isOddDays ? values[idxMedianRef] :
            (values[idxMedianRef] + values[idxMedianRef + 1]) / 2.0f;
        return medianExp;
    }

    private static class EventTree<T> {

//        private TreeSet<Debit> debitSet = new TreeSet<>(theComp);
        private Comparator<T> theComp;
        private Function<T, Float> floatFunction;
        private Function<T, Integer> idFunction;
        private TreeNode<T> rootNode;
        private TreeNode<T> medianNode = null;
        private Map<Integer, TreeNode<T>> nodesById = new TreeMap<>();
        private int size;
        private List<TreeEventHandler<T>> listeners = new LinkedList<>();
        private boolean shouldLog = true;

        public EventTree(Comparator<T> theComp, Function<T, Integer> idFunction, Function<T, Float> floatFunction) {
            this.theComp = theComp;
            this.floatFunction = floatFunction;
            this.idFunction = idFunction;
//            this.rootNode = new TreeNode<>(null);
//            rootNode.isRoot = true;
        }

        public void addListener(TreeEventHandler listener) {
            listeners.add(listener);
        }

        public TreeNode<T> add(T value) {
            TreeNode<T> newNode = null;
            if (rootNode != null) {
                newNode = addRecur(this.rootNode, value);
            }
            else {
                newNode = new TreeNode<>(value);
                setRoot(newNode);
            }

            // adjust median
            adjustMedianForNewNode(newNode);

            size++;
            // track by id
            nodesById.put(getId(newNode.value), newNode);
            return newNode;
        }

        public float getMedian() {
//            int nDebits = mapById.entrySet().size();
            boolean isOddDays = isOddSize();
//            Debit[] trailingAsArray = debitSet.toArray(new Debit[numTrailDebits]);

//            for(int idxDebit = 0; iDebits.hasNext(); idxDebit++) {
//                Debit thisDebit = iDebits.next();
//                if (idxDebit == idxMedianRef) {
//                    meds[0] = thisDebit;
//                    meds[1] = iDebits.next();
//                    break;
//                }
//            }
            float medianExp = isOddDays ? getValueProperty(medianNode.value) :
                (getValueProperty(medianNode.value) + getValueProperty(nextGreaterThan(medianNode).value)) / 2.0f;
            return medianExp;
        }

        public void removeNodeByValueId(int id) {
            TreeNode<T> node = nodesById.remove(id);
            removeNodeFromTree(node);
        }

        private void adjustMedianForNewNode(TreeNode<T> newNode) {
            if (medianNode == null) {
                medianNode = newNode;
            }
            else {
                int compDeltaNodeToMedian = theComp.compare(newNode.value, medianNode.value);
                boolean newSizeIsOdd = !isOddSize(); // we don't adjust size till
                if (compDeltaNodeToMedian == 0) {
                    throw new IllegalArgumentException("new node should not equal current median");
                }
                else if (!newSizeIsOdd && compDeltaNodeToMedian < 0) {
                    // case: we were odd (now even) and just added a node to left, so move median left
                    medianNode = nextLessThan(medianNode);
                    if (shouldLog)
                        System.out.println("moved median left due to new node ==> " + medianNode);
                }
                else if (newSizeIsOdd && compDeltaNodeToMedian > 0) {
                    // case: we were even (now odd) and just added a node to right, so move median to right
                    medianNode = nextGreaterThan(medianNode);
                    if (shouldLog)
                        System.out.println("moved median right due to new node ==> " + medianNode);
                }
                // all other cases keep median where it is
            }
        }

        private TreeNode<T> nextGreaterThan(TreeNode<T> aNode) {
            TreeNode<T> next = null;
            if (aNode.rightNode != null) {
                next = aNode.rightNode;
                // left-most wrt current
                while(next.leftNode != null)
                    next = next.leftNode;
            }
            else {
                next = firstRightParent(aNode);
            }
            return next;
        }

        private TreeNode<T> firstRightParent(TreeNode<T> node) {
            TreeNode<T> found = null;
            while (node.parent != null && found == null) {
                if (node.isParentRight())
                    found = node.parent;
                else
                    node = node.parent;
            }
            return found;
        }

        private TreeNode<T> nextLessThan(TreeNode<T> aNode) {
            TreeNode<T> next = null;
            if (aNode.leftNode != null) {
                next = aNode.leftNode;
                // right-most wrt current
                while(next.rightNode != null)
                    next = next.rightNode;
            }
            else {
                next = firstLeftParent(aNode);
            }
            return next;
        }

        private TreeNode<T> firstLeftParent(TreeNode<T> node) {
            TreeNode<T> found = null;
            while (node.hasParent() && found == null) {
                if (node.isParentLeft())
                    found = node.parent;
                else
                    node = node.parent;
            }
            return found;
        }

        private TreeNode<T> addRecur(TreeNode<T> baseNode, T value) {
            TreeNode<T> valueNode = null;
            int count = 0;
            while (valueNode == null) {
                count++;
                int compNewToBase = theComp.compare(value, baseNode.value);
                if (compNewToBase == 0) {
                    throw new IllegalArgumentException("new node should not compare = 0 base node");
                }
                else if (compNewToBase > 0) {
                    if (baseNode.rightNode != null)
                        baseNode = baseNode.rightNode;
//                        valueNode = addRecur(baseNode.rightNode, value);
                    else {
                        valueNode = baseNode.addNewRight(value);
                    }
                }
                else { // (compNewToBase < 0)
                    if (baseNode.leftNode != null)
//                        valueNode = addRecur(baseNode.leftNode, value);
                        baseNode = baseNode.leftNode;
                    else {
                        valueNode = baseNode.addNewLeft(value);
                    }
                }
            }
            return valueNode;
        }

        private Integer getId(T value) {
            return idFunction.apply(value);
        }

        private Float getValueProperty(T value) {
            return floatFunction.apply(value);
        }

        private boolean isOddSize() {
            return size % 2 != 0;
        }

        /* Does NOT remove from nodesById map. */
        private void removeNodeFromTree(TreeNode<T> node) {
//            if (node == medianNode) {
//                throw new IllegalStateException("should never have to remove the root node");
//            }

            // clip node off of its parent
            if (!node.isRoot) {
                if (node.isLeft)
                    node.parent.leftNode = null;
                else
                    node.parent.rightNode = null;
                // clip parent off of this node
                node.parent = null;
            }

            // reattach the node's children to the proper parent
            if (node.isRoot) {
                if (node.leftNode != null) {
                    setRoot(node.leftNode);
                    if (node.rightNode != null)
                        reparent(node.rightNode);
                }
                else if (node.rightNode != null) {
                    setRoot(node.rightNode);
                    if (node.leftNode != null)
                        reparent(node.leftNode);
                }
            }
            else {
                if (node.leftNode != null)
                    reparent(node.leftNode);
                if (node.rightNode != null)
                    reparent(node.rightNode);
            }

            // adjust median pointer
            int compRemToMedian = theComp.compare(node.value, medianNode.value);
            boolean newSizeIsOdd = !isOddSize();
            if (compRemToMedian == 0) {
                if (newSizeIsOdd) {
                    medianNode = nextGreaterThan(medianNode);
                    if (shouldLog)
                        System.out.println("moved median right due to rem'd node ==> " + medianNode);
                }
                else {
                    medianNode = nextLessThan(medianNode);
                    if (shouldLog)
                        System.out.println("moved median left due to rem'd node ==> " + medianNode);
                }
            }
            else if (!newSizeIsOdd && compRemToMedian > 0) {
                // case: new size is even (previous odd) and we just removed one from right, so median moves left
                medianNode = nextLessThan(medianNode);
                if (shouldLog)
                    System.out.println("moved median left due to rem'd node ==> " + medianNode);
            }
            else if (newSizeIsOdd && compRemToMedian < 0) {
                // case: new size is odd (prev even) and we just removed one from left, so median moves right
                medianNode = nextGreaterThan(medianNode);
                if (shouldLog)
                    System.out.println("moved median right due to rem'd node ==> " + medianNode);
            }

            size--;
        }

        private void setRoot(TreeNode<T> newRoot) {
            rootNode = newRoot;
            rootNode.setIsRoot();
        }

        private void reparent(TreeNode<T> iNode) {
            if (iNode != null) {
                iNode.parent = null;
                attachRecur(rootNode, iNode);
            }
//                attachRecur(medianNode, iNode);
        }

        private void attachRecur(TreeNode<T> rootedNode, TreeNode<T> iNode) {
            int count = 0;
            while(rootedNode != null && !iNode.hasParent()) {
                count++;
                int compare = theComp.compare(iNode.value, rootedNode.value);
                if (compare < 0) {
                    if (rootedNode.leftNode == null) {
                        rootedNode.addLeft(iNode);
                    }
                    else {
                        rootedNode = rootedNode.leftNode;
//                    attachRecur(rootedNode.leftNode, iNode);
                    }
                }
                else if (compare > 0) {
                    if (rootedNode.rightNode == null) {
                        rootedNode.addRight(iNode);
                    }
                    else
//                        attachRecur(rootedNode.rightNode, iNode);
                        rootedNode = rootedNode.rightNode;
                }
                else
                    throw new IllegalArgumentException(
                        "node [" + iNode + "] should not compare equal to rooted node [" + rootedNode + "]");
            }
        }


        public void printTree() {
            StringBuilder sb = new StringBuilder();
            rootNode.depthFirst(new TreeEventHandler<T>() {
                int count;
                @Override
                public void notify(TreeNode<T> node) {
                    count++;
                    sb.append(" ").append(count).append(": ").append(node.value.toString()).append("\n");
                }
            });
            System.out.println(sb.toString());
        }
    }

    /**
     * For custom collection that allows reference to nodes so that I can simply
     * move one link left or right to adjust my median.
     *
     * @param <T>
     */
    private static class TreeNode<T> {
        private TreeNode<T> parent;
        private boolean isLeft;
        private TreeNode<T> leftNode;
        private TreeNode<T> rightNode;
        private T value;
        private boolean isRoot = false;

        public TreeNode(T value) {
            this.value = value;
        }

        private TreeNode<T> addNewLeft(T value) {
            TreeNode<T> newNode;
            newNode = new TreeNode<>(value);
            // double-link
            return addLeft(newNode);
        }

        public void setIsRoot() {
            this.isRoot = true;
            this.parent = null;
        }

        public boolean isParentLeft() {
            return !isLeft;
        }

        public boolean isParentRight() {
            return isLeft;
        }

        public boolean hasParent() {
            return parent != null;
        }

        public TreeNode<T> addLeft(TreeNode<T> aNode) {
            this.leftNode = aNode;
            aNode.parent=this;
            aNode.isLeft = true;
            return aNode;
        }

        public TreeNode<T> addRight(TreeNode<T> aNode) {
            this.rightNode = aNode;
            aNode.parent=this;
            aNode.isLeft = false;
            return aNode;
        }

        public TreeNode<T> addNewRight(T value) {
            TreeNode<T> newNode = new TreeNode<>(value);
            // double-link
            this.rightNode = newNode;
            newNode.parent = this;
            return newNode;
        }

        public void depthFirst(TreeEventHandler<T> handler) {
            if (leftNode != null)
                leftNode.depthFirst(handler);

            handler.notify(this);

            if (rightNode != null) {
                rightNode.depthFirst(handler);
            }
        }

        public String toTreeString() {
            return "Node{" +
                "l=" + leftNode.toTreeString() +
                " v=" + value +
                " r=" + rightNode.toTreeString() +
                '}';
        }

        @Override
        public String toString() {
            return "{node  v=" + value + '}';
        }
    }

    private interface TreeEventHandler<T> {
        void notify(TreeNode<T> node);
    }

    private static class Debit {

        static int idLast;

        int id;
        float amount;

        public Debit(int idx, float amount) {
//            idLast++;
            this.id = idx;
            this.amount = amount;
        }

        public int getId() {
            return id;
        }

        public float getAmount() {
            return amount;
        }

        @Override
        public boolean equals(Object obj) {
            return this.id == ((Debit) obj).id;
        }

        @Override
        public String toString() {
            return "Debit{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
        }
    }

    public static void insertionSortImperative(int[] input) {
        for (int i = 1; i < input.length; i++) {
            int key = input[i];
            int j = i - 1;
            while (j >= 0 && input[j] > key) {
                input[j + 1] = input[j];
                j = j - 1;
            }
            input[j + 1] = key;
        }
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] nd = scanner.nextLine().split(" ");

        int n = Integer.parseInt(nd[0]);

        int d = Integer.parseInt(nd[1]);

        int[] expenditure = new int[n];

        String[] expenditureItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < n; i++) {
            int expenditureItem = Integer.parseInt(expenditureItems[i]);
            expenditure[i] = expenditureItem;
        }

        int result = activityNotifications(expenditure, d);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}
