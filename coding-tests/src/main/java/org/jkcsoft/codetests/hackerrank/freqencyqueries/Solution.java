package org.jkcsoft.codetests.hackerrank.freqencyqueries;

/**
 * @author Jim Coles
 */

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Solution {

    public static final int YES = 1;
    public static final int NO = 0;

    // Complete the freqQuery function below.
    static List<Integer> freqQuery(int[][] queries) {

        // max queries 1,000,000
        // value key range 1 - 10^9

        long startMillis = System.currentTimeMillis();

        List<Integer> results = new LinkedList<>();

        Map<Integer, Integer> table = new TreeMap<>();
        Map<Integer, Integer> freqIndex = new TreeMap<>();
        int getQueryCount = 0;
        int maxFreq = 0;
        int prevResult = -1;
        int prevKey = -1;
        for (int[] query : queries) {
            int oper = query[0];
            int key = query[1];
            if (oper == Query.ADD) {
                updateEntry(table, key, oper, freqIndex);
                maxFreq = Math.max(maxFreq, table.get(key));
            }
            else if (oper == Query.DELETE) {
                updateEntry(table, key, oper, freqIndex);
                maxFreq = Math.max(maxFreq, table.get(key));
            }
            else if (oper == Query.GET) {
                getQueryCount++;
                int result = -1;
                //
                if (prevKey == key) {
                    result = prevResult;
                }
                else {
                    Integer numKeysAtFreq = (key <= maxFreq) ? freqIndex.get(key) : 0;
                    result = numKeysAtFreq != null && numKeysAtFreq > 0 ? YES : NO;
                }
                //
                results.add(result);
                prevResult = result;
                prevKey = key;
            }
        }

        System.out.println(String.format("table size %d; freq index size %d", table.size(), freqIndex.size()));

        System.out.println("timer: " + (System.currentTimeMillis() - startMillis));

        return results;
    }

    private static void updateEntry(Map<Integer, Integer> table, int key, int oper, Map<Integer, Integer> freqIndex) {
        Integer fromFreq = table.get(key);
        if (fromFreq == null) {
            fromFreq = 0;
        }
        Integer toFreq = fromFreq + (oper == Query.ADD ? 1 : -1);

        if (toFreq <= 0)
            // stop tracking keys that go to zero
            table.remove(key);
        else
            table.put(key, toFreq);
        //
        incDecIndex(freqIndex, fromFreq, -1); // one fewer at old freq
        incDecIndex(freqIndex, toFreq, 1); // one more at new freq

    }

    private static void incDecIndex(Map<Integer, Integer> freqIndex, Integer freqKey, int delta) {
        if (freqKey == 0)
            // if freq of key entry goes to zero just de-index
            freqIndex.remove(freqKey);
        else {
            Integer numKeysAtFreq = freqIndex.get(freqKey);
            if (numKeysAtFreq != null)
                numKeysAtFreq = numKeysAtFreq + delta;
            else
                numKeysAtFreq = delta;

            if (numKeysAtFreq == 0)
                freqIndex.remove(freqKey);
            else
                freqIndex.put(freqKey, numKeysAtFreq);
        }
    }

//    private static class ValueTable {
//
//        Map<Integer, Integer> table = new TreeMap<>();
//        Map<Integer>
//
//    }

    private static class Query {
        public static int ADD = 1;
        public static int DELETE = 2;
        public static int GET = 3;

        private int oper;
        private int key;

        public Query(int oper, int key) {
            this.oper = oper;
            this.key = key;
        }

        public int getOper() {
            return oper;
        }

        public int getKey() {
            return key;
        }
    }

//    public static void main(String[] args) {
//        List<List<Integer>> queries = new LinkedList<>();
//        addQuery(queries, 1, 3);
//        addQuery(queries, 2, 3);
//        addQuery(queries, 3, 2);
//        addQuery(queries, 1, 4);
//        addQuery(queries, 1, 5);
//        addQuery(queries, 1, 5);
//        addQuery(queries, 1, 4);
//        addQuery(queries, 3, 2);
//        addQuery(queries, 2, 4);
//        addQuery(queries, 3, 2);
//        //
//        List<Integer> results = freqQuery(queries);
//        results.forEach(System.out::println);
//    }
//
//    private static void addQuery(List<List<Integer>> queries, int oper, int keyOrValue) {
//        queries.add(Arrays.asList(oper, keyOrValue));
//    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int q = Integer.parseInt(bufferedReader.readLine().trim());

        int[][] queries = new int[q][3];

        IntStream.range(0, q).forEach(i -> {
            try {
                int[] query = new int[2];
                List<Integer> qlist = Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                                              .map(Integer::parseInt)
                                              .collect(toList());
                for (int i1 = 0; i1 < qlist.size(); i1++) {
                    query[i1] = qlist.get(i1);
                }
                queries[i] = query;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        List<Integer> ans = freqQuery(queries);

        bufferedWriter.write(
            ans.stream()
               .map(Object::toString)
               .collect(joining("\n"))
                + "\n"
        );

        bufferedReader.close();
        bufferedWriter.close();
    }
}
