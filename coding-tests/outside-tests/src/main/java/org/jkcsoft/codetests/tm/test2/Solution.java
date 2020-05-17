package org.jkcsoft.codetests.tm.test2;

import java.util.Arrays;

/**
 * @author Jim Coles
 */
public class Solution {

    public static void main(String[] args) {
        Solution sol = new Solution();
        System.out.println("no gaps solution 0 => " + sol.solution(new int[] {-1, 0, 1, 2}));
        System.out.println("solution 0 => " + sol.solution(new int[] {5, 5}));
        System.out.println("1-gap solution 1 => " + sol.solution(new int[] {-1, 1, 3}));
        System.out.println("2-gap solution 1 => " + sol.solution(new int[] {-1, 2, 3}));
        System.out.println("3-gap solution 2 => " + sol.solution(new int[] {-1, 3, 5}));
        System.out.println("Max int:" + Integer.MAX_VALUE);

    }

    public int solution(int[] usedRacksPosMeters) {
        // write your code in Java SE 8
        int maxGapMeters = 0;

        Arrays.sort(usedRacksPosMeters);
        int numRacks = usedRacksPosMeters[usedRacksPosMeters.length - 1] - usedRacksPosMeters[0];

        for(int idxUsedRack = 0; idxUsedRack < (usedRacksPosMeters.length - 1); idxUsedRack++) {
            int thisGapRight = usedRacksPosMeters[idxUsedRack + 1] - usedRacksPosMeters[idxUsedRack] - 1;
            maxGapMeters = Math.max(thisGapRight, maxGapMeters);
        }

        return (int) Math.ceil( ((float) maxGapMeters) / 2);
    }

}
