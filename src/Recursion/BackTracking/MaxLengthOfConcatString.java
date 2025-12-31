package Recursion.BackTracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaxLengthOfConcatString {
    //1239
    //https://leetcode.com/problems/maximum-length-of-a-concatenated-string-with-unique-characters/?envType=problem-list-v2&envId=backtracking
    /**
     * You are given an array of strings arr. A string s is formed by the concatenation of a subsequence of arr that has unique characters.
     *
     * Return the maximum possible length of s.
     *
     * A subsequence is an array that can be derived from another array by deleting some or no elements without changing the order of the remaining elements.
     *
     *
     *
     * Example 1:
     *
     * Input: arr = ["un","iq","ue"]
     * Output: 4
     * Explanation: All the valid concatenations are:
     * - ""
     * - "un"
     * - "iq"
     * - "ue"
     * - "uniq" ("un" + "iq")
     * - "ique" ("iq" + "ue")
     * Maximum length is 4.
     * Example 2:
     *
     * Input: arr = ["cha","r","act","ers"]
     * Output: 6
     * Explanation: Possible longest valid concatenations are "chaers" ("cha" + "ers") and "acters" ("act" + "ers").
     * Example 3:
     *
     * Input: arr = ["abcdefghijklmnopqrstuvwxyz"]
     * Output: 26
     * Explanation: The only string in arr has all 26 characters.
     *
     *
     * Constraints:
     *
     * 1 <= arr.length <= 16
     * 1 <= arr[i].length <= 26
     * arr[i] contains only lowercase English letters.
     */

    static void main(String[] args) {
        List<String> inp = new ArrayList<>();
        inp.add("cha");
        inp.add("r");
        inp.add("act");
        inp.add("ers");
        List<List<String>> res = new ArrayList<>();
        int maxLength = 0;
        backTrack(res, new ArrayList<>(), inp, 0);
        for (List<String> list : res){
            String s = String.join("", list);
            if(isUnique(s)){
                maxLength = Math.max(maxLength, s.length());
            }
        }
        System.out.println(maxLength); //800ms
        //method 2
        System.out.println(maxLengthFn(inp)); //24ms
    }

    static void backTrack(List<List<String>> res, List<String> temp, List<String> inp, int start){
        res.add(new ArrayList<>(temp));
        for(int i = start ; i < inp.size() ; i ++){
            temp.add(inp.get(i));
            backTrack(res, temp, inp, i +1);
            temp.remove(temp.size() - 1);
        }
    }

    static boolean isUnique(String s){
        Set<Character> uniqueSet = new HashSet<>();
        for(char i : s.toCharArray()){
            if(uniqueSet.contains(i)){
                return false;
            }
            uniqueSet.add(i);
        }
        return true;
    }

    //method 2
    private static int result = 0;
    static int maxLengthFn(List<String> arr) {
        if (arr == null || arr.size() == 0)    return 0;

        dfs(arr, "", 0);
        return result;
    }

    static void dfs(List<String> arr, String path, int idx) {
        boolean isUniqueChar = isUniqueChars(path);

        if (isUniqueChar) {
            result = Math.max(path.length(), result);
        }

        if (idx == arr.size() || !isUniqueChar) {
            return;
        }

        for (int i = idx; i < arr.size(); i++) {
            dfs(arr, path + arr.get(i), i + 1);
        }

    }

    static boolean isUniqueChars(String s) {
        Set<Character> set = new HashSet<>();
        for (char c : s.toCharArray()) {
            if (set.contains(c)) {
                return false;
            }
            set.add(c);
        }
        return true;
    }
}
