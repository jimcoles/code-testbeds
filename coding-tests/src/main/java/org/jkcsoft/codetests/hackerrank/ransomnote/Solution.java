package org.jkcsoft.codetests.hackerrank.ransomnote;

/**
 * @author Jim Coles
 */
import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Solution {

    // Complete the checkMagazine function below.
    static void checkMagazine(String[] magazine, String[] note) {
        boolean canDo = false;
        HashMap<String, Integer> magHash = hashThing(magazine);
        HashMap<String, Integer> noteHash = hashThing(note);
        for(Map.Entry<String, Integer> noteEntry: noteHash.entrySet() ) {
            canDo = magHash.get(noteEntry.getKey()) >= noteEntry.getValue();
            if (!canDo)
                break;
        }
        System.out.println(canDo ? "Yes" : "No");
    }

    public static HashMap<String, Integer> hashThing(String[] words) {
        HashMap<String, Integer> wordHash = new HashMap();
        for (String word : words) {
            Integer count = wordHash.get(word);
            if (count == null) {
                wordHash.put(word, 1);
            }
            else {
                count++;
                wordHash.put(word, count);
            }
        }
        return wordHash;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String[] mn = scanner.nextLine().split(" ");

        int m = Integer.parseInt(mn[0]);

        int n = Integer.parseInt(mn[1]);

        String[] magazine = new String[m];

        String[] magazineItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < m; i++) {
            String magazineItem = magazineItems[i];
            magazine[i] = magazineItem;
        }

        String[] note = new String[n];

        String[] noteItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < n; i++) {
            String noteItem = noteItems[i];
            note[i] = noteItem;
        }

        checkMagazine(magazine, note);

        scanner.close();
    }
}
