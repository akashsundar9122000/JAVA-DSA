package SlidingWindow;

import java.util.HashSet;
import java.util.Set;

public class SubstringOfSize3WithDistinctCharacters {
    //Leetcode - 1876
    //https://leetcode.com/problems/substrings-of-size-three-with-distinct-characters/?envType=problem-list-v2&envId=sliding-window
    /**
     * A string is good if there are no repeated characters.
     *
     * Given a string s​​​​​, return the number of good substrings of length three in s​​​​​​.
     *
     * Note that if there are multiple occurrences of the same substring, every occurrence should be counted.
     *
     * A substring is a contiguous sequence of characters in a string.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "xyzzaz"
     * Output: 1
     * Explanation: There are 4 substrings of size 3: "xyz", "yzz", "zza", and "zaz".
     * The only good substring of length 3 is "xyz".
     * Example 2:
     *
     * Input: s = "aababcabc"
     * Output: 4
     * Explanation: There are 7 substrings of size 3: "aab", "aba", "bab", "abc", "bca", "cab", and "abc".
     * The good substrings are "abc", "bca", "cab", and "abc".
     *
     *
     * Constraints:
     *
     * 1 <= s.length <= 100
     * s​​​​​​ consists of lowercase English letters.
     */

    static void main() {
        String s = "xyzzaz";
        System.out.println(countGoodSubstrings(s));
    }

    static int countGoodSubstrings(String s) {
        int count = 0;
        if(s.length() < 3){
            return 0;
        }
        String str = s.substring(0,3);
        boolean bool = true;
        Set<Character> res = new HashSet<>();
        for(char c : str.toCharArray()){
            if(res.contains(c)){
                bool = false;
                break;
            }
            res.add(c);
        }
        if(bool){
            count ++;
        }
        for(int i = 1 ; i < s.length() - 3 ; i ++){
            str = s.substring(i, i + 3);
            bool = true;
            res = new HashSet<>();
            for(char c : str.toCharArray()){
                if(res.contains(c)){
                    bool = false;
                    break;
                }
                res.add(c);
            }
            if(bool){
                count ++;
            }
        }
        return count;
    }
}
