package CompanyQuestions.Microsoft;

public class LongestCommonPrefix {
    //14
    //https://leetcode.com/problems/longest-common-prefix/description/
    /**
     * Write a function to find the longest common prefix string amongst an array of strings.
     *
     * If there is no common prefix, return an empty string "".
     *
     *
     *
     * Example 1:
     *
     * Input: strs = ["flower","flow","flight"]
     * Output: "fl"
     * Example 2:
     *
     * Input: strs = ["dog","racecar","car"]
     * Output: ""
     * Explanation: There is no common prefix among the input strings.
     */

    static void main() {
        String[] strs = {"flower","flow","flight"};
        String s = "";
        for(char c: strs[0].toCharArray()){
            String prev = s;
            s += String.valueOf(c);
            for(int i = 1 ; i < strs.length ; i ++){
                if(!strs[i].startsWith(s)){
                    System.out.println(prev);
                    return;
                }
            }
        }
        System.out.println(s);
    }
}
