package Sorting;

import java.util.ArrayList;
import java.util.List;

public class AllMissingNumbers {
    //Leetcode - 448
    //https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/
    //Google
    /**
     * Given an array nums of n integers where nums[i] is in the range [1, n], return an array of all the integers in the range [1, n] that do not appear in nums.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [4,3,2,7,8,2,3,1]
     * Output: [5,6]
     * Example 2:
     *
     * Input: nums = [1,1]
     * Output: [2]
     */

    //It said  from 1 to N so cyclic sort
    static void main() {
        int[] nums = {4,3,2,7,8,2,3,1};
        int i = 0;
        while(i < nums.length){
            int correctIndex = nums[i] - 1;
            if(nums[i] != nums[correctIndex]){
                int temp = nums[i];
                nums[i] = nums[correctIndex];
                nums[correctIndex] = temp;
            } else{
                i++;
            }
        }
        List<Integer> res = new ArrayList<>();
        for(int j = 0 ; j < nums.length ; j ++){
            if(nums[j] != j+1){
                res.add(j + 1);
            }
        }
        System.out.println(res);;
    }
}
