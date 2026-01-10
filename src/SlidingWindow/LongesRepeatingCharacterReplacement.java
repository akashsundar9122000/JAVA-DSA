package SlidingWindow;

import java.util.HashMap;
import java.util.Map;

public class LongesRepeatingCharacterReplacement {
    //424
    //https://leetcode.com/problems/longest-repeating-character-replacement/description/
    /**
     * You are given a string s and an integer k. You can choose any character of the string and change it to any other uppercase English character. You can perform this operation at most k times.
     *
     * Return the length of the longest substring containing the same letter you can get after performing the above operations.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "ABAB", k = 2
     * Output: 4
     * Explanation: Replace the two 'A's with two 'B's or vice versa.
     * Example 2:
     *
     * Input: s = "AABABBA", k = 1
     * Output: 4
     * Explanation: Replace the one 'A' in the middle with 'B' and form "AABBBBA".
     * The substring "BBBB" has the longest repeating letters, which is 4.
     * There may exists other ways to achieve this answer too.
     *
     *
     * Constraints:
     *
     * 1 <= s.length <= 105
     * s consists of only uppercase English letters.
     * 0 <= k <= s.length
     */

    static void main() {
        String s = "AABABBA";
        int k = 1;
        System.out.println(method1(s, k));
        System.out.println(method2(s, k));
    }
    static int method1(String s, int k){
        int l = 0;
        int r = 0;
        int maxLen = 0;
        int maxFreq = 0;
         Map<Character, Integer> res = new HashMap<>();
         while(r < s.length()){
             res.put(s.charAt(r), res.getOrDefault(s.charAt(r), 0 ) + 1);
             maxFreq = Math.max(maxFreq, res.get(s.charAt(r)));
             int changes = r - l + 1 - maxFreq;
             if(changes > k){
                 res.put(s.charAt(l), res.get(s.charAt(l)) - 1);
                 l ++;
             } else{
                 maxLen = Math.max(maxLen, r - l + 1);
             }
             r ++;
         }
         return maxLen;
    }

    static int method2(String s, int k){
        int l = 0;
        int r = 0;
        int maxLen = 0;
        int maxFreq = 0;
        int[] res = new int[26];
        while(r < s.length()){
            res[s.charAt(r) - 'A'] ++;
            //Frequency is nothing but what is the max characters that can be changed.
            maxFreq = Math.max(maxFreq, res[s.charAt(r) - 'A']);
            //so if length - frequency is > k we need to move left pointer because we cant changes those charcters since it is > k.
            int changes = r - l + 1 - maxFreq;
            if(changes > k){
                res[s.charAt(l) - 'A'] --;
                l ++;
            } else{
                //else we can get max length processed
                maxLen = Math.max(maxLen, r -l + 1);
            }
            r ++;
        }
        return maxLen;
    }
}
