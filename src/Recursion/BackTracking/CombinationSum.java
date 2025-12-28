package Recursion.BackTracking;

import java.util.ArrayList;
import java.util.List;

public class CombinationSum {
    //39
    //https://leetcode.com/problems/combination-sum/description/?envType=problem-list-v2&envId=backtracking
    /**
     * Given an array of distinct integers candidates and a target integer target, return a list of all unique combinations of candidates where the chosen numbers sum to target. You may return the combinations in any order.
     *
     * The same number may be chosen from candidates an unlimited number of times. Two combinations are unique if the frequency of at least one of the chosen numbers is different.
     *
     * The test cases are generated such that the number of unique combinations that sum up to target is less than 150 combinations for the given input.
     *
     *
     *
     * Example 1:
     *
     * Input: candidates = [2,3,6,7], target = 7
     * Output: [[2,2,3],[7]]
     * Explanation:
     * 2 and 3 are candidates, and 2 + 2 + 3 = 7. Note that 2 can be used multiple times.
     * 7 is a candidate, and 7 = 7.
     * These are the only two combinations.
     * Example 2:
     *
     * Input: candidates = [2,3,5], target = 8
     * Output: [[2,2,2,2],[2,3,3],[3,5]]
     * Example 3:
     *
     * Input: candidates = [2], target = 1
     * Output: []
     *
     *
     * Constraints:
     *
     * 1 <= candidates.length <= 30
     * 2 <= candidates[i] <= 40
     * All elements of candidates are distinct.
     * 1 <= target <= 40
     */

    static void main() {
        int[] candidates = {2,3,6,7};
        int target = 7;
        List<List<Integer>> ans = new ArrayList<>();
        backTrack(ans, new ArrayList<>(), candidates, target, 0);
        System.out.println(ans);
    }

    static void backTrack(List<List<Integer>> list, List<Integer> temp, int[] nums, int target, int start){
        if(target < 0){
            return;
        }
        if(target == 0){ //if target is 0 we found the exact combination sum
            list.add(new ArrayList<>(temp));
        }
        for(int i = start ; i < nums.length ; i ++){
            temp.add(nums[i]);
            backTrack(list, temp, nums, target - nums[i], i); //we want to subtract current value from target and send to next recursion to find balance sum
            temp.remove(temp.size() - 1); //back track remove last element;
        }
    }
}
