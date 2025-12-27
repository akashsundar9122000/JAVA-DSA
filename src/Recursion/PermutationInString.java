package Recursion;

public class PermutationInString {
    //not yet completed
    //567
    //https://leetcode.com/problems/permutation-in-string/

    /**
     * Given two strings s1 and s2, return true if s2 contains a permutation of s1, or false otherwise.
     *
     * In other words, return true if one of s1's permutations is the substring of s2.
     *
     *
     *
     * Example 1:
     *
     * Input: s1 = "ab", s2 = "eidbaooo"
     * Output: true
     * Explanation: s2 contains one permutation of s1 ("ba").
     * Example 2:
     *
     * Input: s1 = "ab", s2 = "eidboaoo"
     * Output: false
     *
     *
     * Constraints:
     *
     * 1 <= s1.length, s2.length <= 104
     * s1 and s2 consist of lowercase English letters.
     */
    static void main() {
        String s1 = "ab";
        String s2 = "eidbaooo";
        System.out.println(permString("", s1, s2, new boolean[s1.length()]));
    }

    static boolean permString(String processed, String s1, String s2, boolean[] used){
        if(processed.length() == s1. length()){
            if(s2.contains(new String(processed))){
                return true;
            } else{
                return false;
            }
        } else{
            for(int i = 0 ; i < s1.length() ; i ++){
                if(used[i] || i > 0 && s1.charAt(i) == s1.charAt(i - 1) && !used[i - 1]){
                    continue;
                }
                used[i] = true;
                processed += s1.charAt(i);
                permString(processed, s1, s2, used);
                used[i] = false;
                processed = processed.substring(0, processed.length() - 1);
            }
            return false;
        }
    }

}
