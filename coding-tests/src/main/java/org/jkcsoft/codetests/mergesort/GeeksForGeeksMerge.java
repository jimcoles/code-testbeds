package org.jkcsoft.codetests.mergesort;

/**
 * From geeksforgeeks.org .
 * This code is contributed by Rajat Mishra
 *
 * @author Rajat Mishra
 */
public class GeeksForGeeksMerge {

    /* A utility function to print array of size n */
    static void printArray(int[] arr)
    {
        int n = arr.length;
        for (int i = 0; i < n; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }

    // Driver method
    public static void main(String[] args)
    {
        int[] arr = {12, 11, 13, 5, 6, 7};

        System.out.println("Given Array");
        printArray(arr);

        GeeksForGeeksMerge sorter = new GeeksForGeeksMerge();
        sorter.sort(arr, 0, arr.length - 1);

        System.out.println("\nSorted array");
        printArray(arr);
    }

    /**
     *  Main function that sorts arr[l..r] using merge()
     */
    void sort(int[] arr, int leftIdx, int rightIdx)
    {
        if (leftIdx < rightIdx) {
            // Find the middle point
            int middleIdx = (leftIdx + rightIdx) / 2;

            // Sort first and second halves
            sort(arr, leftIdx, middleIdx);
            sort(arr, middleIdx + 1, rightIdx);

            // Merge the sorted halves
            merge(arr, leftIdx, middleIdx, rightIdx);
        }
    }

    /**
     * Merges two subarrays of arr[]. First subarray is arr[l..m] Second subarray is arr[m+1..r]
     */
    void merge(int[] arr, int leftIdx, int middleIdx, int rightIdx)
    {
        // Find sizes of two subarrays to be merged
        int numLeft = middleIdx - leftIdx + 1;
        int numRight = rightIdx - middleIdx;

        /* Create temp arrays */
        int[] leftTmp = new int[numLeft];
        int[] rightTmp = new int[numRight];

        /*Copy data to temp arrays*/
        System.arraycopy(arr, leftIdx, leftTmp, 0, numLeft);
        System.arraycopy(arr, middleIdx + 1, rightTmp, 0, numRight);
//        for (int j = 0; j < numRight; ++j)
//            rightTmp[j] = arr[middleIdx + 1 + j];

        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int idxLeft = 0, idxRight = 0;

        // Initial index of merged subarry array
        int idxPrimary = leftIdx;
        while (idxLeft < numLeft && idxRight < numRight) {
            if (leftTmp[idxLeft] <= rightTmp[idxRight]) {
                arr[idxPrimary] = leftTmp[idxLeft];
                idxLeft++;
            }
            else {
                arr[idxPrimary] = rightTmp[idxRight];
                idxRight++;
            }
            idxPrimary++;
        }

        /* Copy remaining elements of left[] if any */
        while (idxLeft < numLeft) {
            arr[idxPrimary] = leftTmp[idxLeft];
            idxLeft++;
            idxPrimary++;
        }

        /* Copy remaining elements of right[] if any */
        while (idxRight < numRight) {
            arr[idxPrimary] = rightTmp[idxRight];
            idxRight++;
            idxPrimary++;
        }
    }
}
