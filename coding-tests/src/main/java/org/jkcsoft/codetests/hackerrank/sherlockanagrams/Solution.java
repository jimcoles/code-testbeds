package org.jkcsoft.codetests.hackerrank.sherlockanagrams;

/**
 * @author Jim Coles
 */
import java.util.*;

public class Solution {

    // Complete the sherlockAndAnagrams function below.
    static int sherlockAndAnagrams(String s) {

        HashMap<Integer, List<SubString>> indexSubStrByLength = new HashMap<>();

        for(int idxLeftBegin = 0; idxLeftBegin < s.length(); idxLeftBegin++) {
            for(int ndxLeftEnd = idxLeftBegin + 1; ndxLeftEnd <= s.length(); ndxLeftEnd++) {
                SubString subString = new SubString(s, idxLeftBegin, ndxLeftEnd);
                List<SubString> subStrsOfLen = indexSubStrByLength.get(subString.getLength());
                if (subStrsOfLen == null) {
                    subStrsOfLen = new LinkedList<>();
                    indexSubStrByLength.put(subString.getLength(), subStrsOfLen);
                }
                subStrsOfLen.add(subString);
            }
        }
        int cntAnagrams = 0;
        for (Integer strLen : indexSubStrByLength.keySet()) {
            for (SubString subStrLeftComp : indexSubStrByLength.get(strLen)) {
                for (SubString subStrRightComp : indexSubStrByLength.get(strLen)) {
                    if (subStrRightComp.getBeginIdx() > subStrLeftComp.getBeginIdx()
                        && subStrLeftComp.isAnagram(subStrRightComp))
                        cntAnagrams++;
                }
            }
        }

        return cntAnagrams;
    }

    public static class SubString {
        private int beginIdx;
        private int endIdx;
        private char[] chars;
        private HashMap<Character, Integer> charCounts = new HashMap<>();

        public SubString(String base, int beginIdx, int endIdx) {
            this.beginIdx = beginIdx;
            this.endIdx = endIdx;
            chars = base.substring(beginIdx, endIdx).toCharArray();
            // hash of char counts by char (key)
            for (char aChar : chars) {
                Integer cntForChar = charCounts.get(aChar);
                if (cntForChar == null)
                    cntForChar = 0;
                cntForChar = cntForChar + 1;
                charCounts.put(aChar, cntForChar);
            }
        }

        public boolean isAnagram(SubString comp) {
            if (comp.beginIdx == this.beginIdx && comp.endIdx == this.endIdx)
                return false;
            if (comp.getChars().length != this.chars.length)
                return false;
            boolean allCountsMatch = false;
            for (Character character : charCounts.keySet()) {
                allCountsMatch = this.charCounts.get(character).equals(comp.getCharCounts().get(character));
                if (!allCountsMatch)
                    break;
            }
            return allCountsMatch;
        }

        public int getBeginIdx() {
            return beginIdx;
        }

        public int getEndIdx() {
            return endIdx;
        }

        public char[] getChars() {
            return chars;
        }

        public HashMap<Character, Integer> getCharCounts() {
            return charCounts;
        }

        public int getLength() {
            return chars.length;
        }

        @Override
        public String toString() {
            return "SubStr {" + Arrays.toString(chars) + " (" + beginIdx + ", " + endIdx + ")" + '}';
        }
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        testSherlock("abba", 4);
        testSherlock("abcd", 0);
        testSherlock("ifailuhkqq", 3);
        testSherlock("kkkk", 10);
        testSherlock("cdcd", 5);
    }

    private static void testSherlock(String testString, int expected) {
        int actual = sherlockAndAnagrams(testString);
        System.out.println(String.format("Test [%s] expected: [%d] actual [%d] %s",
                                         testString, expected, actual, expected == actual ? "PASS" : "FAIL****"));
    }

//    public static void main(String[] args) throws IOException {
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));
//
//        int q = scanner.nextInt();
//        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
//
//        for (int qItr = 0; qItr < q; qItr++) {
//            String s = scanner.nextLine();
//
//            int result = sherlockAndAnagrams(s);
//
//            bufferedWriter.write(String.valueOf(result));
//            bufferedWriter.newLine();
//        }
//
//        bufferedWriter.close();
//
//        scanner.close();
//    }
}
