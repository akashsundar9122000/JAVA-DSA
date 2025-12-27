package Recursion;

import java.util.ArrayList;
import java.util.List;

public class LeetcodePermutation {
    //46
    //https://leetcode.com/problems/permutations/description/
    /**
     * Given an array nums of distinct integers, return all the possible permutations. You can return the answer in any order.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,2,3]
     * Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
     * Example 2:
     *
     * Input: nums = [0,1]
     * Output: [[0,1],[1,0]]
     * Example 3:
     *
     * Input: nums = [1]
     * Output: [[1]]
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 6
     * -10 <= nums[i] <= 10
     * All the integers of nums are unique.
     */

    static void main() {
        int[] nums = {1,2,3};
        List<List<Integer>> list = new ArrayList<>();
        permutate(list, new ArrayList<>(), nums);
        System.out.println(list);
    }

    static void permutate(List<List<Integer>> list, List<Integer> temp, int[] nums){
        if(temp.size() == nums.length){
            list.add(new ArrayList<>(temp));//New arraylist because if we remove value from temp this alsop removes by refernece so creating new object
        }
        for(int i = 0 ; i < nums.length ; i++){
            if(temp.contains(nums[i])){ //if already exist skip
                continue;
            }
            temp.add(nums[i]);
            permutate(list, temp, nums);
            temp.remove(temp.size() - 1);
        }
    }
}
