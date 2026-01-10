package SlidingWindow;

import java.util.HashMap;
import java.util.Map;

public class NoOfSubstringContainsAll3Characters {
    //1358
    //https://leetcode.com/problems/number-of-substrings-containing-all-three-characters/
    /**
     * Given a string s consisting only of characters a, b and c.
     *
     * Return the number of substrings containing at least one occurrence of all these characters a, b and c.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "abcabc"
     * Output: 10
     * Explanation: The substrings containing at least one occurrence of the characters a, b and c are "abc", "abca", "abcab", "abcabc", "bca", "bcab", "bcabc", "cab", "cabc" and "abc" (again).
     * Example 2:
     *
     * Input: s = "aaacb"
     * Output: 3
     * Explanation: The substrings containing at least one occurrence of the characters a, b and c are "aaacb", "aacb" and "acb".
     * Example 3:
     *
     * Input: s = "abc"
     * Output: 1
     *
     *
     * Constraints:
     *
     * 3 <= s.length <= 5 x 10^4
     * s only consists of a, b or c characters.
     */

    static void main() {
        String s = "abcabc";
        int r = 0;
        Map<Character, Integer> res = new HashMap<>();
        //intitially every char index is -1
        res.put('a', -1);
        res.put('b', -1);
        res.put('c', -1);
        int total = 0;
        while(r < s.length()){
            //ass each charcter pass updating the latese index in map
            res.put(s.charAt(r), r);
            int min = Math.min(res.get('a'), Math.min(res.get('b'), res.get('c')));
            if(min != -1){//if all characters came in map then we can take minimut and add all those index before them to count because all the previous substrings also will contains all a,b,c
                total += min + 1;
            }
            r ++;
        }
        System.out.println(total);
    }
}
