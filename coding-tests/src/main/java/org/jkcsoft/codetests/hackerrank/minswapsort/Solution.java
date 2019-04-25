package org.jkcsoft.codetests.hackerrank.minswapsort;

/**
 * @author Jim Coles
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Solution {

    // Complete the minimumSwaps function below.
    static int minimumSwaps(int[] arr) {
        // e.g.: [7, 1, 3, 2, 4, 5, 6]
        // find the element furthest from home
        int idxSwap = -1;
        int cntSwaps = 0;
        dumpArr(cntSwaps, arr);
        for (int idx = 0; idx < arr.length; idx++) {
            if (!isProper(arr, idx)) {
                swap(arr, idx, arr[idx] - 1);
                cntSwaps++;
                if (!isProper(arr, idx)) {
                    idx--;
                }
                dumpArr(cntSwaps, arr);
            }
        }

        return cntSwaps;
    }

    private static void dumpArr(int swapCnt, int[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append(swapCnt).append(": {");
        if (arr.length < 30)
            for (int idx = 0; idx < arr.length; idx++) {
                sb.append(idx != 0 ? ", " : "")
                  .append(arr[idx]);
            }
        else
            sb.append("(array length too long: " + arr.length);
        sb.append("}");
        log(sb.toString());
    }

    private static boolean isProper(int[] arr, int idx) {
        return arr[idx] == idx + 1;
    }

    private static void swap(int[] arr, int idx1, int idx2) {
        log(String.format("swap %d with %d", idx1, idx2));
        int tmp = arr[idx2];
        arr[idx2] = arr[idx1];
        arr[idx1] = tmp;
    }

    private static void log(String msg) {
        System.out.println(msg);
    }

    private static final Scanner scanner = new Scanner(System.in);

//    public static void main(String[] args) {
//        log(String.format("min swaps result: %d", minimumSwaps(new int[] {7, 1, 3, 2, 4, 5, 6})));
//    }

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        int[] arr = new int[n];

        String[] arrItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < n; i++) {
            int arrItem = Integer.parseInt(arrItems[i]);
            arr[i] = arrItem;
        }

        int res = minimumSwaps(arr);

        bufferedWriter.write(String.valueOf(res));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}
