package SlidingWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepeatedDNASequence {
    //187
    //https://leetcode.com/problems/repeated-dna-sequences/description/?envType=problem-list-v2&envId=sliding-window
    /**
     * The DNA sequence is composed of a series of nucleotides abbreviated as 'A', 'C', 'G', and 'T'.
     *
     * For example, "ACGAATTCCG" is a DNA sequence.
     * When studying DNA, it is useful to identify repeated sequences within the DNA.
     *
     * Given a string s that represents a DNA sequence, return all the 10-letter-long sequences (substrings) that occur more than once in a DNA molecule. You may return the answer in any order.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT"
     * Output: ["AAAAACCCCC","CCCCCAAAAA"]
     * Example 2:
     *
     * Input: s = "AAAAAAAAAAAAA"
     * Output: ["AAAAAAAAAA"]
     *
     *
     * Constraints:
     *
     * 1 <= s.length <= 105
     * s[i] is either 'A', 'C', 'G', or 'T'.
     */

    static void main() {
        String s = "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT";
        if(s.length() < 10){
            return;
        }
        int l = 0;
        int r = 10;
        Map<String, Integer> ans = new HashMap<>();
        List<String> res = new ArrayList<>();
        while(r < s.length() + 1){
            String x = s.substring(l, r);
            ans.put(x, ans.getOrDefault(x, 0) + 1);
            l ++;
            r++;
        }
        for(Map.Entry<String, Integer> map : ans.entrySet()){
            if(map.getValue() > 1){
                res.add(map.getKey());
            }
        }
        System.out.println(res);
    }
}
