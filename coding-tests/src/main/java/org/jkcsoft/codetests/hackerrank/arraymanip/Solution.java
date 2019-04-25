package org.jkcsoft.codetests.hackerrank.arraymanip;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Jim Coles
 */
public class Solution {

    public static final String INTEGRAL_FMT = "%,d";
    public static final String EOL = System.getProperty("line.separator");
    private static final Scanner scanner = new Scanner(System.in);
    // Complete the arrayManipulation function below.
    private static long initMillis;

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] nm = scanner.nextLine().split(" ");

        int n = Integer.parseInt(nm[0]);

        int m = Integer.parseInt(nm[1]);

        int[][] queries = new int[m][3];

        for (int i = 0; i < m; i++) {
            String[] queriesRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            for (int j = 0; j < 3; j++) {
                int queriesItem = Integer.parseInt(queriesRowItems[j]);
                queries[i][j] = queriesItem;
            }
        }

        long result = arrayManipulation(n, queries);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }

    static long arrayManipulation(int numValues, int[][] queries) {
        initMillis = System.currentTimeMillis();
//        return arrayManipFixedHolder(numValues, queries);
//        return arrayManipHash(numValues, queries);
//        long sum = 0;
//        log("go. num queries: " + fmt(queries.length));
//        for (int[] query : queries) {
//            sum += query[2];
//        }
//        log("sum: " + fmt(sum));
//        return 1;

        RangeTrackingSolution solution = new RangeTrackingSolution();
        return solution.arrayManipIntervalTracking(numValues, queries);
    }

    private static String fmt(long l) {
        return String.format(INTEGRAL_FMT, l);
    }

    private static String fmt(int i) {
        return String.format(INTEGRAL_FMT, i);
    }

    private static void log(String msg) {
        System.out.println(fmtMillisAsFloat() + " - " + msg);
    }

    private static String fmtMillisAsFloat() {
        return String.format("%.3f", (System.currentTimeMillis() - initMillis) / 1000.0d);
    }

    public static void mainBrute(String[] args) throws IOException {
        int maxValue = (int) Math.pow(10, 9);
        int maxNumValues = (int) Math.pow(10, 7);
        int maxNumQueries = (int) (2 * Math.pow(10, 5));
        //
        int numValues;
        //
        testSimpleSample();
        //
        testSmall(maxValue);
        //
//        testMaxValueOverflow(maxValue, maxNumValues);
        //
        testMaxAll(maxValue, maxNumValues, maxNumQueries);
    }

    //=========================================
    // Hard-coded test cases
    //=========================================

    private static void testSimpleSample() {
        int numValues;
        numValues = 5;
        testArrayManip(numValues, new int[][]{
            {1, 2, 100},
            {2, 5, 100},
            {3, 4, 100}
        });
    }

    private static void testMaxAll(int maxValue, int maxNumValues, int maxNumOpers) {
        int[][] maxInput = new int[maxNumOpers][3];
        for (int idxOpers = 0; idxOpers < maxNumOpers; idxOpers++) {
            int idx1 = 0;
//            int idx1 = idxOpers;
            maxInput[idxOpers][0] = (idx1 + 1) % maxNumValues;
            maxInput[idxOpers][1] = (idx1 + 2) % maxNumValues;
            maxInput[idxOpers][2] = maxValue;
        }
        testArrayManip(maxNumValues, maxInput);
    }

    private static void testMaxValueOverflow(int maxValue, int maxNumValues) {
        int numValues;
        numValues = maxNumValues;
        testArrayManip(numValues, new int[][]{
            {1, numValues, maxValue},
            {1, numValues, maxValue}
        });
    }

    private static void testSmall(int maxValue) {
        int numValues;
        numValues = 10;
        testArrayManip(numValues, new int[][]{
            {1, numValues, maxValue},
            {1, numValues, maxValue}
        });
    }

    private static void testArrayManip(int maxNumValues, int[][] queries) {
        log("=============================");
        log("result max value: " + arrayManipulation(maxNumValues, queries));
    }

    public static class RangeTrackingSolution {

        public long arrayManipIntervalTracking(int numValues, int[][] queries) {
            log("pre sort");
            Arrays.sort(queries,
                        Comparator.<int[]>comparingInt(intAr -> intAr[0])
                            .thenComparing(intAr -> intAr[1] - intAr[0])
            );
            log("post sort");
            AddValueQuery addValueQuery = null;

            ValueDocument doc = new ValueDocument(numValues);
            doc.insertRangeNode(new RangeNode(1, numValues, 0));
            log("Doc: " + doc);
            for (int[] arQuery : queries) {
                addValueQuery = new AddValueQuery(arQuery[0], arQuery[1], arQuery[2]);
                doc.applyMod(addValueQuery);
                if (doc.shouldLog())
                    log("Doc: " + doc);
            }
            return doc.getMaxValue();
        }

    /*
    Naive algorithm is N x M time complexity -- too high.

    Advanced algorithm:
     1. reduce complexity based on NQ number of queries
     2. reduce complexity based on N number of values
     3. take advantage
     - query interval is continuous
     - avoid duplicate computations

     - pre-index query intervals for faster comparison
     - index active set of "interval nodes"
     - Possible sort orders
        - order queries by value DESC.
          drop any slot, s,  where
            current max > s max + ( (next value) * num remaining queries )
        - order slots by start index

     - Upon first query, track one "Node" with "value" and range of indices
     - Upon next query, Qi, evaluate interval overlaps of Qi with node set (i - 1).
       Create new nodes for unique new overlaps, as needed.
     */

        /**
         * Wrap the query notion for clarity.
         */
        public static class AddValueQuery {
            private IntRange intRange;
            private int value;

            public AddValueQuery(int ndx1, int ndx2, int value) {
                this.intRange = new IntRange(ndx1, ndx2);
                this.value = value;
            }

            public IntRange getIntRange() {
                return intRange;
            }

            public int getValue() {
                return value;
            }

            @Override
            public String toString() {
                return "Mod: add " + value + " " + intRange;
            }
        }

        public static class ValueDocument {

            private int numLines;
            private TreeMap<Integer, RangeNode> rangeNodeMap = new TreeMap<>();
            //        private List<RangeNode> nodeList =
            private int modCount;
            private long maxValue;
            private boolean testDrops = false;
            private int lastQueryNdx1 = 0;

            public ValueDocument(int numLines) {
                this.numLines = numLines;
            }

            public TreeMap<Integer, RangeNode> getRangeNodeMap() {
                return rangeNodeMap;
            }

            public void insertRangeNode(RangeNode newRange) {
                if (shouldLog())
                    log("\t\tinserting node: " + newRange);
                rangeNodeMap.put(newRange.getIntRange().getNdx1(), newRange);
            }

            /**
             *          0        1         2         3         4
             *          1234567890123456789012345678901234567890
             *  Before  |--|    |---------|  |--| |---| |----|
             *  Query              |------------------|
             *                        +V    V +V V +V
             *  After   |--|    |--|------|--|--|-|---| |----|
             *
             * new map = head + (tail - head of tail).
             *
             * @param addValueQuery
             * @return
             */
            public void applyMod(AddValueQuery addValueQuery) {
                modCount++;
//        RangeNode exactNd1Node = rangeDoc.get(addValueQuery.getNdx1());
//        RangeNode exactNd2Node = rangeDoc.get(addValueQuery.getNdx2());
                // 1. Break existing nodes into
                //    - head and tail, unaffected by query
                //    - sequence of fully-overlapped nodes
                //    - 0, 1, or 2 nodes with partial overlap at the ends of the query
                if (shouldLog())
                    log("\t" + addValueQuery);

                LinkedList<RangeNode> overlapNodes;
                IntRange qr = addValueQuery.getIntRange();
                if (qr.getNdx1() > lastQueryNdx1) {
                    testDrops = true;
                    this.lastQueryNdx1 = qr.getNdx1();
                }

                Map.Entry<Integer, RangeNode> headGrayEntry = getRangeNodeMap().floorEntry(qr.getNdx1());
                // handle boundary case nodes
                RangeNode headGrayNode = headGrayEntry != null ? headGrayEntry.getValue() : null;
                RangeNode headPartial = null;
                if (headGrayNode != null && headGrayNode.getIntRange().getNdx2() >= qr.getNdx1()) {
                    headPartial = headGrayNode;
                    // query partially overlaps last head node
                    Intersection headIntx = headPartial.getIntRange().intersection(addValueQuery.intRange);
                    // track
                    RangeNode tailRemaining = headPartial.copy();

                    // adjust existing node: clip overlap tail, no value change
                    headPartial.getIntRange().setNdx2(headIntx.intersection.ndx1 - 1);
                    // add new node for overlap, (next step will increment)
                    RangeNode newOverlapNode = new RangeNode(headIntx.intersection.ndx1,
                                                             headIntx.intersection.ndx2,
                                                             headPartial.getValue());
                    insertRangeNode(newOverlapNode);
//            doc.setMaxValueIf(newOverlapNode);

                    // possibly add new remaining tail of original node that was just split up
                    if (tailRemaining.getIntRange().getNdx2() > newOverlapNode.getIntRange().getNdx2()) {
                        tailRemaining.getIntRange().setNdx1(newOverlapNode.getIntRange().getNdx2() + 1);
                        insertRangeNode(tailRemaining);
                    }
//            log("Branched partial head overlap: " + newOverlapNode);
                }
                //
                overlapNodes = new LinkedList<>(
                    getRangeNodeMap().tailMap(qr.getNdx1(), true)
                                     .headMap(qr.getNdx2(), true).values()
                );
                //
                RangeNode tailGrayNode = overlapNodes.getLast();
                if (tailGrayNode != null && tailGrayNode.getIntRange().getNdx2() > qr.getNdx2()) {
                    RangeNode tailRemainder = tailGrayNode.copy();
                    tailGrayNode.getIntRange().setNdx2(qr.getNdx2());
                    tailRemainder.getIntRange().setNdx1(qr.getNdx2() + 1);
                    insertRangeNode(tailRemainder);
                }
                // increment all existing overlapped nodes
                for (RangeNode node : overlapNodes) {
                    node.incValue(addValueQuery.getValue());
                    setMaxValueIf(node);
                }

                // drop any nodes that can not possibly increase beyond current max
                if (testDrops)
                    dropDeadNodes(qr.getNdx1());
            }

            private void dropDeadNodes(int newGroupStartNdx) {
                Collection<RangeNode> nodes = rangeNodeMap.values();
                List<RangeNode> drops = null;
                for (RangeNode node : nodes) {
                    if (node.getIntRange().getNdx2() < newGroupStartNdx) {
                        if (node.getValue() < maxValue) {
                            if (drops == null)
                                drops = new LinkedList<>();
                            drops.add(node);
                        }
                    }
                    else
                        break;
                }
                if (drops != null) {
                    drops.forEach((RangeNode node) -> {
                        rangeNodeMap.remove(node.getIntRange().getNdx1());
                    });
                    if (shouldLog())
                        log("dropping " + drops.size() + " nodes; current nodes count: " + rangeNodeMap.size());
                }
                testDrops = false;
            }

            public void setMaxValueIf(RangeNode node) {
                if (node.getValue() > this.maxValue) {
                    this.maxValue = node.getValue();
                    this.testDrops = true;
                    if (shouldLog())
                        log("\t\tnew max range => " + node);
                }
            }

            public long getMaxValue() {
                return maxValue;
            }

            public boolean shouldLog() {
                return modCount <= 100 || modCount % 1000 == 0;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder(" modCount=" + modCount
                                                         + " node count=" + rangeNodeMap.size()
                                                         + " max=" + fmt(maxValue)
                                                         + EOL
                );
                if (numLines <= 80) {
                    int width = (maxValue > 0 ? (int) Math.floor(Math.log(this.maxValue)) + 1 : 1) + 1;
                    String format = "%" + width + "d";
                    Collection<RangeNode> nodes = rangeNodeMap.values();
                    for (RangeNode node : nodes) {
                        for (int ndx = node.getIntRange().ndx1; ndx <= node.getIntRange().getNdx2(); ndx++) {
                            sb.append(
                                (ndx == node.getIntRange().ndx1 ? "|" : "") +
                                    String.format(format, node.getValue()) +
                                    (ndx == node.getIntRange().ndx2 ? "|" : "")
                            );
                        }
                    }
                }
                else
                    sb.append("(doc too large)");

                return sb.toString();
            }

        }

        /**
         * We create 0 or more new {@link RangeNode}'s for each query we evaluate.
         * Represents a contiguous range of array positions and
         * the current value for that range.
         */
        public static class RangeNode implements Comparable<RangeNode> {
            //        private RangeNode branchedFrom;
            private IntRange intRange;
            private long value;

            public RangeNode(int ndx1, int ndx2, long value) {
                //          this.branchedFrom = branchedFrom;
                this.intRange = new IntRange(ndx1, ndx2);
                this.value = value;
            }

//        public RangeNode getBranchedFrom() {
//            return branchedFrom;
//        }

            public IntRange getIntRange() {
                return intRange;
            }

            public long getValue() {
                return value;
            }

            public void setValue(long value) {
                this.value = value;
            }

            public void incValue(long value) {
                this.value += value;
            }

            public RangeNode copy() {
                return new RangeNode(this.intRange.getNdx1(), this.intRange.getNdx2(), this.getValue());
            }

            @Override
            public int compareTo(RangeNode right) {
                return this.getIntRange().compareTo(right.getIntRange());
            }

            @Override
            public String toString() {
                return "Node: " + intRange + "=" + value;
            }
        }

        public static class IntRange implements Comparable<IntRange> {
            private int ndx1; // 1-based first
            private int ndx2; // 1-based last

            public IntRange(int ndx1, int ndx2) {
                this.ndx1 = ndx1;
                this.ndx2 = ndx2;
            }

            public int getNdx1() {
                return ndx1;
            }

            public void setNdx1(int ndx) {
                this.ndx1 = ndx;
            }

            public int getNdx2() {
                return ndx2;
            }

            public void setNdx2(int ndx) {
                this.ndx2 = ndx;
            }

            @Override
            public int compareTo(IntRange right) {
                return Integer.compare(this.ndx1, right.getNdx1());
            }

            public boolean overlap(IntRange right, boolean includeHead, boolean includeTail) {
                return ndexBetween(right.getNdx1(), includeHead, includeTail) ||
                    ndexBetween(right.getNdx2(), includeHead, includeTail);
            }

            /**
             *
             * @param argRange
             * @return
             */
            public Intersection intersection(IntRange argRange) {
                Intersection intersection = new Intersection();
                int first = Math.max(this.ndx1, argRange.getNdx1());
                int last = Math.min(this.ndx2, argRange.getNdx2());
                if (last >= first)
                    intersection.intersection = new IntRange(first, last);

                return intersection;
            }

            public boolean ndexBetween(int ndx, boolean inclusiveHead, boolean includeTail) {
                return ndx > this.ndx1 && ndx < this.ndx2
                    || (inclusiveHead && (ndx == this.ndx1))
                    || (includeTail && (ndx == this.ndx2));
            }

            @Override
            public String toString() {
                return "(" + this.ndx1 + "," + this.ndx2 + ")";
            }

        }

        public static class Intersection {
            //        IntRange baseRemHead;
            IntRange intersection;
            IntRange baseRemainder;
        }

    }

    public static class HashSlotSolution {

        public long arrayManipHash(int numValues, int[][] queries) {
            log("== Hash Table Value Holder ==");
            ValueMapWrap valueMapWrap = new ValueMapWrap();
            for (int[] query : queries) {
                for (int ndx = query[0]; ndx <= query[1]; ndx++) {
                    valueMapWrap.increment(ndx, query[2]);
                }
            }
            //
            return valueMapWrap.getMaxValue();
        }

        public static class ValueMapWrap {

            private Map<Integer, Long> valueMap = new HashMap<>();
            private long maxValue = 0;

            public long getMaxValue() {
                return maxValue;
            }

            public Map<Integer, Long> getValueMap() {
                return valueMap;
            }

            private void increment(int ndx, int value) {
                Long currValue = valueMap.get(ndx);
                if (currValue == null) {
                    currValue = (long) value;
                }
                else {
                    currValue += value;
                }
                valueMap.put(ndx, currValue);
                maxValue = Math.max(maxValue, currValue);
            }

        }
    }

    public static class FixedHolderSolution {

        private static long arrayManipulation(int numValues, int[][] queries) {
            log("== Fixed Array Value Holder ==");
            QueryVector table = new QueryVector(numValues);
            for (int[] query : queries) {
                table.addOverlay(query[0] - 1, query[1] - 1, query[2], queries.length / 100);
            }
            return table.getMaxValue();
        }

        public static class QueryVector {

            private long[] vector;
            private int numQueries;
            private long maxValue = Long.MIN_VALUE;

            public QueryVector(int nvalues) {
                log("nvalues: " + nvalues);
                vector = new long[nvalues];
                //          printVals();
            }

            public void addOverlay(int idx1, int idx2, int value, int logEvery) {
                numQueries++;
                boolean log = logEvery > 0 && (numQueries % logEvery) == 0;
                if (log) {
                    String msg = "oper: " + idx1 + " " + idx2 + " " + value;
                    log(msg);
                }

                for (int idx = idx1; idx <= idx2; idx++) {
                    vector[idx] += value;
                    maxValue = Math.max(maxValue, vector[idx]);
                }
                if (log)
                    printMax();

//            printVals();
            }

            private void printVals() {
                StringBuilder sb = new StringBuilder();
                for (long i : vector) {
                    sb.append(i + ", ");
                }
                log("interim: " + sb.toString());
                printMax();
            }

            private void printMax() {
                log("max running after query [" + numQueries + "]: " + maxValue);
            }

            public long getMaxValue() {
                return this.maxValue;
            }
        }

    }

}
