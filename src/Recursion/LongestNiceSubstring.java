package Recursion;

import java.util.HashSet;
import java.util.Set;

public class LongestNiceSubstring {
    //1763
    //https://leetcode.com/problems/longest-nice-substring/description/?envType=problem-list-v2&envId=sliding-window
    /**
     * A string s is nice if, for every letter of the alphabet that s contains, it appears both in uppercase and lowercase. For example, "abABB" is nice because 'A' and 'a' appear, and 'B' and 'b' appear. However, "abA" is not because 'b' appears, but 'B' does not.
     *
     * Given a string s, return the longest substring of s that is nice. If there are multiple, return the substring of the earliest occurrence. If there are none, return an empty string.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "YazaAay"
     * Output: "aAa"
     * Explanation: "aAa" is a nice string because 'A/a' is the only letter of the alphabet in s, and both 'A' and 'a' appear.
     * "aAa" is the longest nice substring.
     * Example 2:
     *
     * Input: s = "Bb"
     * Output: "Bb"
     * Explanation: "Bb" is a nice string because both 'B' and 'b' appear. The whole string is a substring.
     * Example 3:
     *
     * Input: s = "c"
     * Output: ""
     * Explanation: There are no nice substrings.
     *
     *
     * Constraints:
     *
     * 1 <= s.length <= 100
     * s consists of uppercase and lowercase English letters.
     */

    static void main() {
        String s = "YazaAay";
        System.out.println(longestNiceSubstring(s));
    }
    static String longestNiceSubstring(String s) {
        Set<Character> res = new HashSet<>();
        for(char c : s.toCharArray()){
            res.add(c);
        }
        for(int i = 0 ; i < s.length() ; i ++){
            if(res.contains(Character.toLowerCase(s.charAt(i))) && res.contains(Character.toUpperCase(s.charAt(i)))){
                continue;
            }
            //if both small and caps not available we need to split string into 2 parts and do recursion
            String left = longestNiceSubstring(s.substring(0, i));
            String right = longestNiceSubstring(s.substring(i + 1));
            return left.length() >= right.length() ? left : right;
        }
        return s;
    }
}
