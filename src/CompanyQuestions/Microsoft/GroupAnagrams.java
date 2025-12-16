package CompanyQuestions.Microsoft;

import java.util.*;

public class GroupAnagrams {
    //Leetcode - 49
    //https://leetcode.com/problems/group-anagrams/
    /**
     * Given an array of strings strs, group the anagrams together. You can return the answer in any order.
     *
     *
     *
     * Example 1:
     *
     * Input: strs = ["eat","tea","tan","ate","nat","bat"]
     *
     * Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
     *
     * Explanation:
     *
     * There is no string in strs that can be rearranged to form "bat".
     * The strings "nat" and "tan" are anagrams as they can be rearranged to form each other.
     * The strings "ate", "eat", and "tea" are anagrams as they can be rearranged to form each other.
     * Example 2:
     *
     * Input: strs = [""]
     *
     * Output: [[""]]
     *
     * Example 3:
     *
     * Input: strs = ["a"]
     *
     * Output: [["a"]]
     */

    static void main() {
        String[] strs = {"eat","tea","tan","ate","nat","bat"};
        HashMap<String, ArrayList<String>> res = new HashMap<>();
        for(int i = 0 ; i < strs.length ; i ++){
            char[] arr = strs[i].toCharArray();
            Arrays.sort(arr);
            String s = new String(arr);
            ArrayList<String> ans = res.getOrDefault(s,new ArrayList<String>());
            ans.add(strs[i]);
            res.put(s, ans);
        }
        System.out.println(new ArrayList<>(res.values())); //6ms solution
//        List<List<String>> finalAns = new ArrayList<>();
//        res.forEach((key, value) ->{  //8ms solution
//            finalAns.addAll(Collections.singleton(value));
//        });
//        System.out.println(finalAns);

    }
}
