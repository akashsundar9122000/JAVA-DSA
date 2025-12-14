package Sorting;

import java.util.ArrayList;
import java.util.List;

public class FindAllDuplicates {
    //Leetcode - 442
    //https://leetcode.com/problems/find-all-duplicates-in-an-array/description/
    /**
     * Given an integer array nums of length n where all the integers of nums are in the range [1, n] and each integer appears at most twice, return an array of all the integers that appears twice.
     *
     * You must write an algorithm that runs in O(n) time and uses only constant auxiliary space, excluding the space needed to store the output
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [4,3,2,7,8,2,3,1]
     * Output: [2,3]
     * Example 2:
     *
     * Input: nums = [1,1,2]
     * Output: [1]
     * Example 3:
     *
     * Input: nums = [1]
     * Output: []
     */
    //cyclic sort
    //microsoft
    static void main() {
        int[] nums = {4,3,2,7,8,2,3,1};
        int i = 0;
        while(i < nums.length){
            int correct = nums[i] - 1;
            if(nums[i] != nums[correct]){
                int temp = nums[i];
                nums[i] = nums[correct];
                nums[correct] = temp;
            } else{
                i++;
            }
        }
        List<Integer> res = new ArrayList<>();
        for(i = 0 ; i < nums.length ; i ++){
            if(nums[i] != i + 1){
                res.add(nums[i]);
            }
        }
        System.out.println(res);
    }
}
