package SlidingWindow;

import java.util.Arrays;

public class MinWindowSubstring {
    //76
    //https://leetcode.com/problems/minimum-window-substring/description/?envType=problem-list-v2&envId=sliding-window
    /**
     * Given two strings s and t of lengths m and n respectively, return the minimum window substring of s such that every character in t (including duplicates) is included in the window. If there is no such substring, return the empty string "".
     *
     * The testcases will be generated such that the answer is unique.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "ADOBECODEBANC", t = "ABC"
     * Output: "BANC"
     * Explanation: The minimum window substring "BANC" includes 'A', 'B', and 'C' from string t.
     * Example 2:
     *
     * Input: s = "a", t = "a"
     * Output: "a"
     * Explanation: The entire string s is the minimum window.
     * Example 3:
     *
     * Input: s = "a", t = "aa"
     * Output: ""
     * Explanation: Both 'a's from t must be included in the window.
     * Since the largest window of s only has one 'a', return empty string.
     *
     *
     * Constraints:
     *
     * m == s.length
     * n == t.length
     * 1 <= m, n <= 105
     * s and t consist of uppercase and lowercase English letters.
     */

    static void main() {
        String s = "ADOBECODEBANC";
        String t = "ABC";
        int[] hash = new int[256];
        Arrays.fill(hash,0);
        int n = s.length();
        int m = t.length();
        int l = 0;
        int r = 0;
        int startIndex = -1;
        int count = 0;
        int minLen = Integer.MAX_VALUE;
        //first we need to add count for all characters in string t.
        for(int i = 0 ; i < m ; i ++){
            hash[t.charAt(i)] ++;
        }
        //now the characters in t will have count > 0
        while(r < n){
            if(hash[s.charAt(r)] > 0){
                //if characters present in t comes we need to increase count.
                count ++;
            }
            hash[s.charAt(r)] --; //subtract value for the char in hash since it is processed.
            while(count == m){ //when count == m that is all chars in t comes in the substring we need to reduce the window to find min length
                if(r - l + 1 < minLen){ //if the current len is the min len then that will be start index for the substring
                    minLen = r - l + 1;
                    startIndex = l;
                }
                hash[s.charAt(l)] ++;//now we are moving the left pointer adding the value in hash
                if(hash[s.charAt(l)] > 0){
                    //it the value is > 0 that is the char in t string is not now in substring we need to decrease the count so we can process next substrings
                    count --;
                }
                l ++;
            }
            r ++;
        }
        if(startIndex == -1){
            System.out.println("");
        }
        else{
            System.out.println(s.substring(startIndex, startIndex + minLen));
        }
    }
}
