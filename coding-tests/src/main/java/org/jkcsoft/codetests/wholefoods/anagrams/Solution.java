package org.jkcsoft.codetests.wholefoods.anagrams;

/**
 * @author Jim Coles
 */
import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;



class Result {

    /*
     * Complete the 'getMinimumDifference' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     *  1. STRING_ARRAY a
     *  2. STRING_ARRAY b
     */

    public static List<Integer> getMinimumDifference(List<String> a, List<String> b) {
        // Write your code here
        List<Integer> results = new LinkedList<>();
        Iterator<String> bIter = b.iterator();
        for(String aString : a) {
            String bString = bIter.next();
            results.add(anagramTest(aString, bString));
        }
        return results;
    }

    public static int anagramTest(String left, String right) {
        int numCharChanges = -1;
        if (left.length() == right.length()) {
            numCharChanges = 0;
            Map<Character, Integer> leftCounts = new TreeMap<>();
            Map<Character, Integer> rightCounts = new TreeMap<>();
            loadMap(left, leftCounts);
            loadMap(right, rightCounts);
            for (Character character : leftCounts.keySet()) {
                int leftCnt = leftCounts.get(character) != null ? leftCounts.get(character) : 0;
                int rightCnt = rightCounts.get(character) != null ? rightCounts.get(character) : 0;
                int lacking = leftCnt - rightCnt;
                numCharChanges += lacking > 0 ? lacking : 0;
            }
        }
        return numCharChanges;
    }


    private static void loadMap(String string, Map<Character, Integer> charCounts) {
        for (char c : string.toCharArray()) {
            Integer cnt = charCounts.get(c);
            if (cnt == null) {
                charCounts.put(c, 1);
            }
            else {
                charCounts.replace(c, cnt + 1);
            }
        }
    }

}


public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int aCount = Integer.parseInt(bufferedReader.readLine().trim());

        List<String> a = IntStream.range(0, aCount).mapToObj(i -> {
            try {
                return bufferedReader.readLine();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        })
                                  .collect(toList());

        int bCount = Integer.parseInt(bufferedReader.readLine().trim());

        List<String> b = IntStream.range(0, bCount).mapToObj(i -> {
            try {
                return bufferedReader.readLine();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        })
                                  .collect(toList());

        List<Integer> result = Result.getMinimumDifference(a, b);

        bufferedWriter.write(
            result.stream()
                  .map(Object::toString)
                  .collect(joining("\n"))
                + "\n"
        );

        bufferedReader.close();
        bufferedWriter.close();
    }
}
