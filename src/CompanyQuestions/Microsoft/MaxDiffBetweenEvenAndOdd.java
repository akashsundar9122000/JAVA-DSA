package CompanyQuestions.Microsoft;

import java.util.HashMap;

public class MaxDiffBetweenEvenAndOdd {
    //Leetcode - 3442
    //https://leetcode.com/problems/maximum-difference-between-even-and-odd-frequency-i/description/
    /**
     *You are given a string s consisting of lowercase English letters.
     *
     * Your task is to find the maximum difference diff = freq(a1) - freq(a2) between the frequency of characters a1 and a2 in the string such that:
     *
     * a1 has an odd frequency in the string.
     * a2 has an even frequency in the string.
     * Return this maximum difference.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "aaaaabbc"
     *
     * Output: 3
     *
     * Explanation:
     *
     * The character 'a' has an odd frequency of 5, and 'b' has an even frequency of 2.
     * The maximum difference is 5 - 2 = 3.
     * Example 2:
     *
     * Input: s = "abcabcab"
     *
     * Output: 1
     *
     * Explanation:
     *
     * The character 'a' has an odd frequency of 3, and 'c' has an even frequency of 2.
     * The maximum difference is 3 - 2 = 1.
     */

    static void main() {
        String s = "aaaaabbc";
        HashMap<String, Integer> res = new HashMap<>();
        for(char c : s.toCharArray()){
            int val = 0;
            if(res.containsKey(String.valueOf(c))){
                val = res.get(String.valueOf(c));
            }
            val+=1;
            res.put(String.valueOf(c), val);
        }
        int maxOdd = 0;
        int maxEven = s.length();
        for(String key : res.keySet()){
            int cal = res.get(key);
            if(cal % 2 == 0){
                if(cal < maxEven){
                    maxEven = cal;
                }
            } else{
                if(cal > maxOdd){
                    maxOdd = cal;
                }
            }
        }
        System.out.println(maxOdd - maxEven);
    }
}

/**
 * alternate
 * Map<Character, Integer> c = new HashMap<>();
 *         for (char ch : s.toCharArray()) {
 *             c.put(ch, c.getOrDefault(ch, 0) + 1); //best to put vals
 *         }
 *         int maxOdd = 1, minEven = s.length();
 *         for (int value : c.values()) {
 *             if (value % 2 == 1) {
 *                 maxOdd = Math.max(maxOdd, value);
 *             } else {
 *                 minEven = Math.min(minEven, value);
 *             }
 *         }
 *         return maxOdd - minEven;
 */