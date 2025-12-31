package Recursion.BackTracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Subset2 {
    //90
    //https://leetcode.com/problems/subsets-ii/description/?envType=problem-list-v2&envId=backtracking
    /**
     * Given an integer array nums that may contain duplicates, return all possible subsets (the power set).
     *
     * The solution set must not contain duplicate subsets. Return the solution in any order.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,2,2]
     * Output: [[],[1],[1,2],[1,2,2],[2],[2,2]]
     * Example 2:
     *
     * Input: nums = [0]
     * Output: [[],[0]]
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 10
     * -10 <= nums[i] <= 10
     */
    static void main() {
        int[] nums = {1,2,2};
        method1(nums); //3ms solution
        method2(nums); //11ms solution
    }

    static void method1(int[] nums){
        Arrays.sort(nums);
        List<List<Integer>> list = new ArrayList<>();
        list.add(new ArrayList<>());
        duplicateSubsetBackTrack(list, new ArrayList<Integer>(), nums, 0);
        System.out.println(list);
    }

    static void duplicateSubsetBackTrack(List<List<Integer>> list, List<Integer> temp, int[] nums, int start){
        for(int i = start ; i < nums.length ; i ++){
            if(i > start && nums[i] == nums[i - 1]){
                continue;
            }
            temp.add(nums[i]);
            list.add(new ArrayList<>(temp));
            duplicateSubsetBackTrack(list, temp, nums, i + 1);
            temp.remove(temp.size() - 1);
        }
    }

    static void method2(int[] nums){
        Arrays.sort(nums);
        List<List<Integer>> list = new ArrayList<>();
        duplicateSubsetBackTrackMethod2(list, new ArrayList<Integer>(), nums, 0);
        System.out.println(list);
    }

    static void duplicateSubsetBackTrackMethod2(List<List<Integer>> list, List<Integer> temp, int[] nums, int start){
        if(!list.contains(temp)){
            list.add(new ArrayList<>(temp));
        }
        for(int i = start ; i < nums.length ; i ++){
            temp.add(nums[i]);
            duplicateSubsetBackTrackMethod2(list, temp, nums, i + 1);
            temp.remove(temp.size() - 1);
        }
    }
}
