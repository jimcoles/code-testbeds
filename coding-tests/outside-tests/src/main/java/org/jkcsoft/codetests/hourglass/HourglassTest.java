/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2019 through present.
 *
 * Licensed under the following license agreement:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.codetests.hourglass;

import java.io.*;
import java.util.*;

/**
 * @author Jim Coles
 */
public class HourglassTest {

    private static final Scanner scanner = new Scanner(System.in);
    static Set<Coords> shapeZerosIndexes = new HashSet<>();

    static {
        shapeZerosIndexes.add(new Coords(1, 0));
        shapeZerosIndexes.add(new Coords(1, 2));
    }

    static int flatIdx(int width, int row, int col) {
        return row * width + col;
    }

    // Complete the hourglassSum function below.
    static int hourglassSum(int[][] arr) {
        List<HourGlass> hourGlasses = new LinkedList<>();
        for (int idxRow = 0; idxRow < (6 - 2); idxRow++) {
            for (int idxCol = 0; idxCol < (6 - 2); idxCol++) {
                HourGlass hg = new HourGlass();
                for (int idxHgRow = 0; idxHgRow < 3; idxHgRow++) {
                    for (int idxHgCol = 0; idxHgCol < 3; idxHgCol++) {
                        int idxBigRow = idxRow + idxHgRow;
                        int idxBigCol = idxCol + idxHgCol;
                        hg.add(idxHgRow, idxHgCol, arr[idxBigRow][idxBigCol]);
                    }
                }
//                if (hg.validate())
                hourGlasses.add(hg);
            }
        }
        int maxSum = Integer.MIN_VALUE;
        for (HourGlass hg : hourGlasses) {
            maxSum = Math.max(maxSum, hg.countWithFilter());
        }
        return maxSum;
    }

    static boolean isFiltered(int idxFlat) {
        boolean must = false;
        for (Coords coord : shapeZerosIndexes) {
            if (coord.flatIdx == idxFlat) {
                must = true;
                break;
            }
        }
        return must;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Sum output: " +
                               hourglassSum(
                                   new int[][]{
                                       new int[]{1, 1, 1, 0, 0, 0},
                                       new int[]{0, 1, 0, 0, 0, 0},
                                       new int[]{1, 1, 1, 0, 0, 0},
                                       new int[]{0, 9, 2, -4, -4, 0},
                                       new int[]{0, 0, 0, -2, 0, 0},
                                       new int[]{0, 0, -1, -2, -4, 0}
                                   }
                               )
        );
    }

    public static void mainThing(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int[][] arr = new int[6][6];

        for (int i = 0; i < 6; i++) {
            String[] arrRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            for (int j = 0; j < 6; j++) {
                int arrItem = Integer.parseInt(arrRowItems[j]);
                arr[i][j] = arrItem;
            }
        }

        int result = hourglassSum(arr);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }

    public static class Coords {
        int idxRow;
        int idxCol;
        int flatIdx;

        public Coords(int idxRow, int idxCol) {
            this.idxRow = idxRow;
            this.idxCol = idxCol;
            this.flatIdx = flatIdx(3, idxRow, idxCol);
        }
    }

    public static class HourGlass {
        //        boolean valid = true;
        int countOfValues = 0;
        private int[] values = new int[9];

        public void add(int idxCol, int idxRow, int value) {
            values[flatIdx(3, idxCol, idxRow)] = value;
        }

        public int countWithFilter() {
            for (int idxHgRow = 0; idxHgRow < 3; idxHgRow++) {
                for (int idxHgCol = 0; idxHgCol < 3; idxHgCol++) {
                    int idxFlat = flatIdx(3, idxHgRow, idxHgCol);
                    if (!isFiltered(idxFlat)) {
                        countOfValues += values[idxFlat];
                    }
                }
            }
            return this.countOfValues;
        }

    }

}
