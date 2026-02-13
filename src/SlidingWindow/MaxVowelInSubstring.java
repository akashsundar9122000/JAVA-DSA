package SlidingWindow;

public class MaxVowelInSubstring {
    //1456
    //https://leetcode.com/problems/maximum-number-of-vowels-in-a-substring-of-given-length/description/?envType=problem-list-v2&envId=sliding-window
    /**
     * Given a string s and an integer k, return the maximum number of vowel letters in any substring of s with length k.
     *
     * Vowel letters in English are 'a', 'e', 'i', 'o', and 'u'.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "abciiidef", k = 3
     * Output: 3
     * Explanation: The substring "iii" contains 3 vowel letters.
     * Example 2:
     *
     * Input: s = "aeiou", k = 2
     * Output: 2
     * Explanation: Any substring of length 2 contains 2 vowels.
     * Example 3:
     *
     * Input: s = "leetcode", k = 3
     * Output: 2
     * Explanation: "lee", "eet" and "ode" contain 2 vowels.
     *
     *
     * Constraints:
     *
     * 1 <= s.length <= 105
     * s consists of lowercase English letters.
     * 1 <= k <= s.length
     */

    static void main() {
        String s = "weallloveyou";
        int k = 7;
        System.out.println(maxVowels(s, k));
    }

    static int maxVowels(String s, int k) {
        int max = 0;
        int c = 0;
        for(int i = 0 ; i < k ; i++){
            if(isVowel(s.charAt(i))){
                c ++;
            }
        }
        if(c == k){
            return k;
        }
        max = Math.max(max, c);
        for(int i = 1 ; i < s.length() - k + 1 ; i ++){
            if(i - 1 >= 0 && isVowel(s.charAt(i - 1))){
                c --;
            }
            if(isVowel(s.charAt(k + i - 1))){
                c ++;
            }
            if(c == k){
                return k;
            }
            max = Math.max(c, max);
        }
        return max;
    }

    static boolean isVowel(char c){
        return c == 'a' || c == 'e' || c == 'i' || c =='o' || c == 'u';
    }
}
