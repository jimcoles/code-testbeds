/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2019 through present.
 *
 * Licensed under the following license agreement:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.codetests.hackerrank.valleys;

/**
 * @author Jim Coles
 */
public class ValleyTest {

    // Complete the countingValleys function below.
    static int countingValleys(int n, String s) {
        int valleyCount = 0;
        char[] chars = s.toCharArray();
        int prevAlt = 0;
        int altitude = 0;
        boolean inValley = false;
        for (char c : chars) {
            int altMove = altChange(c);
            altitude += altMove;
            inValley = inValley || isValley(altitude);
            if (inValley && atSeaLevel(altitude)) {
                valleyCount++;
                inValley = false;
            }
            prevAlt = altitude;
        }
        return valleyCount;
    }

    private static boolean atSeaLevel(int altitude) {
        return altitude == 0;
    }

    private static int altChange(char c) {
        return c == 'D' ? -1 : 1;
    }

    private static boolean isValley(int altitude) {
        return altitude <= -2;
    }

    public void testCounter() {
        testHike("simplest 1", "DDUU", 1);
        testHike("simplest 2", "DDUUDDUU", 2);
        testHike("> 2 steps", "DDDUUUDDUUDDD", 2);
        testHike("ignore little peak in valley", "DDDDUUDDUUUU", 1);
        //
        testHike("not deep enuf", "UDDUU", 0);
        testHike("did not get out", "DDDUU", 0);
        testHike("mtn only", "UUDDDUU", 0);
        //
        testHike("one char edge", "D", 0);
        testHike("zero char edge", "", 0);
    }

    private void testHike(String name, String hike, int expected) {
        int actual = countingValleys(8, hike);
        System.out.println("case '" + name + "' [" + hike + "] " +
                               "actual " + actual +
                               " expected: " + expected +
                               " => " + ((actual == expected) ? "PASS" : "FAILED***") );
    }

    public static void main(String[] args) {
        ValleyTest vt = new ValleyTest();
        vt.testCounter();
    }
}
