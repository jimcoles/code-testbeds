package org.jkcsoft.codetests.tm.test1;

/**
 * @author Jim Coles
 */
public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println("expect a => [" + solution.solution("zzaabbxy") + "]");
    }

    String solution(String S) {
        int[] occurrences = new int[26];
        for (char ch : S.toCharArray()) {
            occurrences[ch - 'a']++;
        }

        char best_char = 'a';
        int best_res = 0;

        for (int i = 0; i < 26; i++) {
            if (occurrences[i] > best_res) {
                best_char = (char) ((int) 'a' + i);
                best_res = occurrences[i];
            }
        }

        return Character.toString(best_char);
    }
}
