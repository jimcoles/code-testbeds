package org.jkcsoft.codetests;

import java.util.Arrays;

/**
 * @author Jim Coles
 */
public class PosIntArrayTest {

    public static void main(String[] args) {
        PosIntArrayTest solution = new PosIntArrayTest();
        testSolution("simple", 3,  solution, new int[]{1, 2, 4});
        testSolution("simple sort", 3, solution, new int[]{4, 2, 1});
        testSolution("some negatives", 3, solution, new int[]{-1, 1, 2, -4});
        testSolution("some negatives no 1", 1, solution, new int[]{-1, 2, -4});
        testSolution("quickly 1", 1, solution, new int[]{2, 3, 4});
    }

    private static void testSolution(String name, int expected, PosIntArrayTest solution, int[] A) {
        int sol = solution.solution(A);
        System.out.println("solution for [" + name + "] expected "+expected+": " + sol + " P/F => "
         + (expected == sol ? "PASS" : "FAIL***") );
    }

    public int solution(int[] A) {
        // write your code in Java SE 8
        Arrays.sort(A);
        int value = 0;
        int idx = 0;
        boolean gapFound = false;
        while (idx < A.length) {
            int currInt = A[idx];
            if (idx == 0) {
                if (currInt > 0 && currInt != 1) {
                    value = 1;
                    gapFound = true;
                    break;
                }
            }
            else {
                if (currInt == 1) {
                    // next
                }
                else {
                    int prevInt = A[idx - 1];
                    if (currInt > 0 && currInt > (prevInt + 1)) {
                        value = prevInt + 1;
                        gapFound = true;
                        break;
                    }
                }
            }
            idx++;
        }
        if (!gapFound)
            // array had not gap; value is highest value + 1
            value = A[A.length - 1] + 1;
        // bump to 1 if negative
        if (value < 1)
            value = 1;
        return value;
    }
}
