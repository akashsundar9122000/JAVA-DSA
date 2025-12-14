package Sorting;

public class FindDuplicate {
    //Leetcoode - 287
    //https://leetcode.com/problems/find-the-duplicate-number/description/
    //Microsoft and Amazon
    /**
     * Given an array of integers nums containing n + 1 integers where each integer is in the range [1, n] inclusive.
     *
     * There is only one repeated number in nums, return this repeated number.
     *
     * You must solve the problem without modifying the array nums and using only constant extra space.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,3,4,2,2]
     * Output: 2
     * Example 2:
     *
     * Input: nums = [3,1,3,4,2]
     * Output: 3
     * Example 3:
     *
     * Input: nums = [3,3,3,3,3]
     * Output: 3
     */

    static void main() {
        int[] nums = {1,3,4,2,2};
        int i = 0;
        while(i < nums.length){
            if(nums[i] != i + 1){ //we can only perform sort if the value != i + 1 that is 3 will be in 2nd index if it fails then 3 is in correct index
                int correct = nums[i] - 1;
                if(nums[i] != nums[correct]){ //if this is not correct we need to swap
                    int temp = nums[i];
                    nums[i] = nums[correct];
                    nums[correct] = temp;
                } else{ //if that is correct then this is the duplicate
                    System.out.println(nums[i]);
                    return;
                }
            } else{
                i ++;
            }
        }
    }
}
