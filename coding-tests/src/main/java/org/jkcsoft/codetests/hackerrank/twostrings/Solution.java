package org.jkcsoft.codetests.hackerrank.twostrings;

import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

/**
 * @author Jim Coles
 */
public class Solution {

    // Complete the twoStrings function below.
    static String twoStrings(String s1, String s2) {
        boolean haveCommon = false;
        String shortest = null;
        String longest = null;
        if (s1.length() < s2.length()) {
            shortest = s1;
            longest = s2;
        }
        else {
            shortest = s2;
            longest = s1;
        }
        HashSet<Character> shortHash = new HashSet<>(shortest.length());
        for (char c : shortest.toCharArray()) {
            shortHash.add(c);
        }
        for (char c : longest.toCharArray()) {
            if (shortHash.contains(c)) {
                haveCommon = true;
                break;
            }
        }

        return haveCommon ? "YES" : "NO";
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int q = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int qItr = 0; qItr < q; qItr++) {
            String s1 = scanner.nextLine();

            String s2 = scanner.nextLine();

            String result = twoStrings(s1, s2);

            bufferedWriter.write(result);
            bufferedWriter.newLine();
        }

        bufferedWriter.close();

        scanner.close();
    }
}
