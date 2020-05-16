package org.jkcsoft.codetests.hackerrank.arraymanip;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author Jim Coles
 */
public class Solution {

    public static final String INTEGRAL_FMT = "%,d";
    public static final String EOL = System.getProperty("line.separator");
    private static final Scanner scanIoStream = new Scanner(System.in);
    // Complete the arrayManipulation function below.
    private static long initMillis;

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));
//        MapReduceSolution solution = new MapReduceSolution();
        SortReductionSolution solution = new SortReductionSolution();
        //
        long result = solution.solve(System.in);
        //
        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanIoStream.close();
    }

    private static void out(String msg, Object... args) {
        System.out.println(fmtMillisAsFloat() + " - " + MessageFormat.format(msg, args));
    }

    private static String fmtMillisAsFloat() {
        return String.format("%.3f", (System.currentTimeMillis() - initMillis) / 1000.0d);
    }

    public static class SortReductionSolution {

        ToIntFunction<int[]> getStartIdx = intAr -> intAr[0];
        ToIntFunction<int[]> getEndIdx = intAr -> intAr[1];
        ToIntFunction<int[]> getRangeSize = intAr -> intAr[1] - intAr[0];
        ToIntFunction<int[]> getValue = intAr -> intAr[2];

        public long solve(InputStream is) {

            Scanner scanner = new Scanner(is);
            String[] nm = scanner.nextLine().split(" ");
            int numSlots = Integer.parseInt(nm[0]);
            int numDeltas = Integer.parseInt(nm[1]);

            int[][] queries = new int[numDeltas][3];

            for (int i = 0; i < numDeltas; i++) {
                String[] queriesRowItems = scanner.nextLine().split(" ");
                scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

                for (int j = 0; j < 3; j++) {
                    int queriesItem = Integer.parseInt(queriesRowItems[j]);
                    queries[i][j] = queriesItem;
                }
            }

            out("start");

            out("applying {1} deltas to {0} slots", numSlots, numDeltas);

//            out("pre sort");
            Comparator<int[]> compByStartThenByEnd =
                Comparator
                    .comparingInt(getStartIdx)
                    .thenComparingInt(getEndIdx).reversed();
//            Arrays.sort(queries, compByStartThenByEnd);
//            out("post sort");

            /*
            for each (unique) Slot start index, i, and max slot index, j
                make list of all queries with that start index i
                make list (overlaps, i) of all slots corresponding to other slot start indices
                for each in Slot index overlaps, in order of highest to lowest
                    create QueryRange; sum that range
                    for next QueryRange, add its query value to previous
                }
            }
            */

            Map<Integer, SortedSet<int[]>> queriesGroupedAndSorted =
                Arrays.stream(queries)
                      .collect(
                          Collectors.groupingBy(
                              getStartIdx::applyAsInt,
                              new Collector<int[], SortedSet<int[]>, SortedSet<int[]>>() {

                                  private final HashSet<Characteristics> characteristics = new HashSet<>();

                                  @Override
                                  public Supplier<SortedSet<int[]>> supplier() {
                                      return () -> new TreeSet<>(Comparator.comparingInt(getEndIdx).reversed());
                                  }

                                  @Override
                                  public BiConsumer<SortedSet<int[]>, int[]> accumulator() {
                                      return Set::add;
                                  }

                                  @Override
                                  public BinaryOperator<SortedSet<int[]>> combiner() {
                                      return (ints, ints2) -> null;
                                  }

                                  @Override
                                  public Function<SortedSet<int[]>, SortedSet<int[]>> finisher() {
                                      return (aSet) -> aSet;
                                  }

                                  @Override
                                  public Set<Characteristics> characteristics() {
                                      return characteristics;
                                  }
                              }
                          )
                      );

            List<Integer> startIndexesSorted =
                queriesGroupedAndSorted.keySet().stream().sorted().collect(Collectors.toList());

            startIndexesSorted.stream().forEach(
                (groupStartIdx) -> {
                    int[] baseGroupFirstQuery = getFirstQueryForGroup(queriesGroupedAndSorted, groupStartIdx);
                    List<Integer> overlappingGroupsStartIdx =
                        startIndexesSorted.stream()
                                         .filter(
                                             (testIdx) -> {
                                                 return testIdx > groupStartIdx
                                                     && testIdx >= getStartIdx.applyAsInt(baseGroupFirstQuery)
                                                        && testIdx <= getEndIdx.applyAsInt(baseGroupFirstQuery);
                                             }
                                         )
                                         .collect(Collectors.toList());

                    overlappingGroupsStartIdx.sort(Comparator.comparingInt(Integer::intValue).reversed());

                    overlappingGroupsStartIdx.forEach(
                        new Consumer<Integer>() {
                            @Override
                            public void accept(Integer slotIdx) {
                                setSlotQueryRangesForGroup(queriesGroupedAndSorted, groupStartIdx, slotIdx);
                            }
                        }
                    );
                }
            );

            long maxValue = 0;
            return maxValue;
        }

        private int[] getFirstQueryForGroup(Map<Integer, SortedSet<int[]>> queriesGroupedAndSorted, Integer testIdx) {
            return queriesGroupedAndSorted.get(testIdx).first();
        }

        SortedMap<Integer, SlotSum> slotSumsByIndex = new TreeMap<>();
        SortedSet<SlotSum> slotSums = new TreeSet<>(Comparator.comparingLong(SlotSum::getValue));
        Map<Integer, QueryRange> headSlotRangeByGroupIndex = new TreeMap<>();

        private void setSlotQueryRangesForGroup(Map<Integer, SortedSet<int[]>> queriesByStartThenEnd,
                                                Integer groupSlotStartIdx, Integer slotIdx)
        {
            SortedSet<int[]> groupQueriesDesc = queriesByStartThenEnd.get(groupSlotStartIdx);
            QueryRange currentHeadRange = headSlotRangeByGroupIndex.get(groupSlotStartIdx);
            int relQIndexStart = currentHeadRange != null ? currentHeadRange.getQueryStopIdx() + 1 : 0;
            int relQIndexStop = relQIndexStart;
            long rangeSum = 0;
            for (int[] query : groupQueriesDesc) {
                // iterate thru queries of this group until one does not contain slotIdx
                if (doesQueryContainSlot(query, slotIdx)) {
                    rangeSum += getValue.applyAsInt(query);
                    relQIndexStop++;
                }
                else
                    break;
            }
            QueryRange newRange = new QueryRange(relQIndexStart, relQIndexStop, rangeSum, currentHeadRange);
            headSlotRangeByGroupIndex.put(groupSlotStartIdx, newRange);
            resetSlotSum(groupSlotStartIdx, slotIdx, newRange);
        }

        private void resetSlotSum(Integer activeGroupSlotIdx, Integer slotIdx, QueryRange newRange) {
            SlotSum theSlotSum = slotSumsByIndex.get(slotIdx);
            if (theSlotSum == null) {
                theSlotSum = new SlotSum(slotIdx);
                slotSumsByIndex.put(slotIdx, theSlotSum);
            }
            else {
                slotSums.remove(theSlotSum);
            }
            theSlotSum.addRange(newRange);
            // reset ordering if sum was not new
            slotSums.add(theSlotSum);

            // Purge slots that can no longer possibly exceed the current max slot
            Set<SlotSum> purgeList =
            slotSums.headSet(slotSums.last())
                    .stream()
                    .filter((slotSum -> slotSum.getSlotIndex() < activeGroupSlotIdx))
                    .collect(Collectors.toSet());
            purgeList.forEach(slotSum -> {
                slotSumsByIndex.remove(slotSum.getSlotIndex(), slotSum);
                slotSums.remove(slotSum);
            });
        }

        private boolean doesQueryContainSlot(int[] query, Integer slotIdx) {
            return getStartIdx.applyAsInt(query) <= slotIdx && getEndIdx.applyAsInt(query) >= slotIdx;
        }
    }

    public static class SlotSum {
        private int slotIndex;
        private LongAdder value = new LongAdder();
        private List<QueryRange> queryRanges = new LinkedList<>();

        public SlotSum(int slotIndex) {
            this.slotIndex = slotIndex;
        }

        public int getSlotIndex() {
            return slotIndex;
        }

        public void addRange(QueryRange range) {
            queryRanges.add(range);
            value.add(range.getCummulativeValue());
        }

        public long getValue() {
            return value.longValue();
        }
    }

    public static class QueryRange {
        private int queryStartIdx;
        private int queryStopIdx;
        private long rangeValue;
        private long cummulativeValue;
        private QueryRange previous;

        public QueryRange(int queryStartIdx, int queryStopIdx, long rangeValue, QueryRange previous) {
            this.queryStartIdx = queryStartIdx;
            this.queryStopIdx = queryStopIdx;
            this.rangeValue = rangeValue;
            this.previous = previous;
            firePreviousChange();
        }

        public int getQueryStartIdx() {
            return queryStartIdx;
        }

        public int getQueryStopIdx() {
            return queryStopIdx;
        }

        public long getRangeValue() {
            return rangeValue;
        }

        public QueryRange getPrevious() {
            return previous;
        }

        private void firePreviousChange() {
            this.cummulativeValue = rangeValue + (previous != null ? previous.getCummulativeValue() : 0L);
        }

        public void setPrevious(QueryRange previous) {
            this.previous = previous;
            firePreviousChange();
        }

        public long getCummulativeValue() {
            return cummulativeValue;
        }
    }
    public static class MapReduceSolution {

        public long solve(InputStream in) throws IOException {
            // build the pipeline ...
            Scannerator scannerator = new Scannerator(System.in);
            // eat the first line
            String argLine = scannerator.getScanner().nextLine();
            Scanner argScanner = new Scanner(argLine).useDelimiter(" ");
            int numSlots = argScanner.nextInt();
            int numDeltas = argScanner.nextInt();
            out("applying {1} deltas to {0} slots", numSlots, numDeltas);

            // state managed by m/r pipeline
            LongAdder seq = new LongAdder();
            Map<Integer, ValueSlot> valueSlotTable = new HashMap<>();
            long maxResult = 0;
            LedgerSum maxLedger = null;
            RangeNode maxRange = null;

            // -----------------------------------------------
            // Define our map and reduce functions

            Function<? super String, AddValueRecord> unmarshallAddOper = (line) -> {
                AddValueRecord addValueRecord = null;
                if (line != null) {
                    Scanner scanner = new Scanner(line).useDelimiter(" ");
                    addValueRecord =
                        new AddValueRecord(nextTimeSeq(seq), scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
                }
                return addValueRecord;
            };

            // Create or lookup value slots
            Function<? super AddValueRecord, Stream<LedgerSum>> joinLedgerSums = (addValueRecord) -> {
                IntStream slotIds =
                    IntStream.range(addValueRecord.getNdx1(), addValueRecord.getNdx2() + 1);
                Map<LedgerSum, Set<ValueSlot>> slotsByLedgerSum = new HashMap<>();
                //
                slotIds.forEach(slotId -> {
                                    ValueSlot slot = null;
                                    if (valueSlotTable.containsKey(slotId)) {
                                        slot = valueSlotTable.get(slotId);
                                    }
                                    else {
                                        slot = new ValueSlot(slotId);
                                        slot.setLedgerSum(LedgerSum.EMPTY);
                                        valueSlotTable.put(slotId, slot);
                                    }

                                    Set<ValueSlot> ledgerSlotSet = slotsByLedgerSum.get(slot.getLedgerSum());
                                    if (ledgerSlotSet == null) {
                                        ledgerSlotSet = new HashSet<>();
                                        slotsByLedgerSum.put(slot.getLedgerSum(), ledgerSlotSet);
                                    }
                                    ledgerSlotSet.add(slot);
                                }
                );
                //
                Set<LedgerSum> updatedLedgers = new HashSet<>();
                slotsByLedgerSum.entrySet().forEach((slotsByLedger) -> {
                                                        LedgerSum newLedgerSum = new LedgerSum(slotsByLedger.getKey(), addValueRecord);
                                                        slotsByLedger.getValue().forEach(
                                                            (slot) -> slot.setLedgerSum(newLedgerSum)
                                                        );
                                                        updatedLedgers.add(newLedgerSum);
                                                    }
                );
                return updatedLedgers.parallelStream();
            };

            BinaryOperator<LedgerSum> maxLedgerValue =
                BinaryOperator.maxBy(Comparator.comparingLong(LedgerSum::getValue));

            BinaryOperator<RangeNode> maxRangeNodeVal =
                BinaryOperator.maxBy(Comparator.comparingLong(RangeNode::getValue));

            // end m/r functions
            // -----------------------------------------------------------------

            Stream<String> recordStream = scannerator.getLineStream();
            // The final step: Evaluate the stream pipeline

            out("start");

//            List<AddValueRecord> sortedOpers =
//                recordStream.map(unmarshallAddOper).sorted(Comparator.comparingInt(AddValueRecord::getNdx1)).collect(
//                    Collectors.toList());

/*
            long sum = recordStream.map(unmarshallAddOper)
                                   .map(AddValueRecord::getValue)
                                   .mapToLong(Integer::longValue)
                                   .sum();

            out("sum all: {0}", sum);
*/

            maxRange = recordStream
                .map(unmarshallAddOper)
                .flatMap(new RangeMapper())
//                .flatMap(joinLedgerSums)
                .parallel()
                .reduce(maxRangeNodeVal)
                .orElse(null)
            ;
            out("Max Slot: {0} ", maxRange);
            maxResult = Objects.requireNonNull(maxRange).getValue();

            return maxResult;
        }

        private long nextTimeSeq(LongAdder seq) {
            seq.increment();
            return seq.longValue();
        }

    }

    public static class QueryPipeline implements Consumer<AddValueRecord> {

        @Override
        public void accept(AddValueRecord record) {

        }

    }

    public static class AddValueRecord {
        private long timeSeq;
        private int ndx1;
        private int ndx2;
        private int value;

        public AddValueRecord(long timeSeq, int ndx1, int ndx2, int value) {
            this.timeSeq = timeSeq; // a key
            this.ndx1 = ndx1;
            this.ndx2 = ndx2;
            this.value = value;
        }

        public long getTimeSeq() {
            return timeSeq;
        }

        public int getNdx1() {
            return ndx1;
        }

        public int getNdx2() {
            return ndx2;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Mod: add " + value + " r1 " + ndx1 + " r2 " + ndx2;
        }
    }

    public static class ValueSlot {

        private int slotIndex;
        private LedgerSum ledgerSum;

        public ValueSlot(int slotIndex) {
            this.slotIndex = slotIndex;
        }

        public int getSlotIndex() {
            return slotIndex;
        }

        public LedgerSum getLedgerSum() {
            return ledgerSum;
        }

        public void setLedgerSum(LedgerSum ledgerSum) {
            this.ledgerSum = ledgerSum;
        }

        @Override
        public String toString() {
            return MessageFormat.format("Slot index={0} ledgerSum={1}", slotIndex, ledgerSum);
        }
    }

    public static class RangeMapper implements Function<AddValueRecord, Stream<? extends RangeNode>> {

        @Override
        public Stream<? extends RangeNode> apply(AddValueRecord record) {
            Set<RangeNode> rangeNodes = new HashSet<>();
            return rangeNodes.parallelStream();
        }

    }
    /**
     * We create 0 or more new {@link RangeNode}'s for each query we evaluate.
     * Represents a contiguous range of array positions and
     * the current value for that range.
     */
    public static class RangeNode implements Comparable<RangeNode> {
        //        private RangeNode branchedFrom;
        private int querySeqNum;
        private IntRange intRange;
        private long value;

        public RangeNode(int queryId, int ndx1, int ndx2, long value) {
            //          this.branchedFrom = branchedFrom;
            this.querySeqNum = queryId;
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

        public void incValue(int querySeqNum, long value) {
            this.querySeqNum = querySeqNum;
            this.value += value;
        }

        public int getQuerySeqNum() {
            return querySeqNum;
        }

        public void setQuerySeqNum(int querySeqNum) {
            this.querySeqNum = querySeqNum;
        }

        public RangeNode copy() {
            return new RangeNode(this.querySeqNum, this.intRange.getNdx1(), this.intRange.getNdx2(), this.getValue());
        }

        @Override
        public int compareTo(RangeNode right) {
            return this.getIntRange().compareTo(right.getIntRange());
        }

        @Override
        public String toString() {
            return "Node(" + querySeqNum + "): " + intRange + "=" + value;
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
        public Intersection intersection(
            IntRange argRange)
        {
            Intersection
                intersection = new Intersection();
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

        public int size() {
            return ndx2 - ndx1 + 1;
        }
    }

    public static class Intersection {
        //        IntRange baseRemHead;
        IntRange intersection;
        IntRange baseRemainder;
    }

    public static class LedgerSum {
        public static final LedgerSum EMPTY = new LedgerSum(null, new AddValueRecord(-1L, 0, 0, 0));
        //        private long id; // key
//        private LedgerSum firstList;
//        private AddValueRecord lastEntry;
        // computed and cached
        private LongAdder value = new LongAdder();

        public LedgerSum(LedgerSum firstList, AddValueRecord lastEntry)
        {
            //
            if (firstList != null)
                value.add(firstList.getValue());
            value.add(lastEntry.getValue());
        }

        public long getValue() {
            return value.longValue();
        }

        @Override
        public String toString() {
            return MessageFormat.format("[ledgerSum list value={0}]", value);
        }
    }

    /** Wrap a java.io.{@link InputStream} as a spliterator source. */
    public static class Scannerator {
        //        private InputStream is;
        private Scanner scanner;
        private Stream<String> lineStream;
        private long count = 0;

        public Scannerator(InputStream is) {
//            this.is = is;
            scanner = new Scanner(is);
            Spliterator<String> scanningSpliterator = Spliterators.spliteratorUnknownSize(
                new Iterator<String>() {
                    @Override
                    public boolean hasNext() {
                        return scanner.hasNext();
                    }

                    @Override
                    public String next() {
                        count++;
                        double log = Math.log10(count);
                        if (log == Math.floor(log))
                            out("query count: {0}", count);
                        return scanner.nextLine();
                    }
                },
//                Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL
                Spliterator.IMMUTABLE | Spliterator.NONNULL
            );
            lineStream = StreamSupport.stream(scanningSpliterator, false);
        }

        public Scanner getScanner() {
            return scanner;
        }

        public Stream<String> getLineStream() {
            return lineStream;
        }
    }

}
