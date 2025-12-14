package Sorting;

public class FirstMissingPositiveInteger {
    //Leetcode - 41
    //https://leetcode.com/problems/first-missing-positive/
    /**
     * Given an unsorted integer array nums. Return the smallest positive integer that is not present in nums.
     *
     * You must implement an algorithm that runs in O(n) time and uses O(1) auxiliary space.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,2,0]
     * Output: 3
     * Explanation: The numbers in the range [1,2] are all in the array.
     * Example 2:
     *
     * Input: nums = [3,4,-1,1]
     * Output: 2
     * Explanation: 1 is in the array but 2 is missing.
     * Example 3:
     *
     * Input: nums = [7,8,9,11,12]
     * Output: 1
     * Explanation: The smallest positive integer 1 is missing.
     */

    //Cyclic sort because O(N)
    static void main() {
        int[] nums = {7,8,9,11,12};
        int i = 0;
        while(i < nums.length){
            int correct = nums[i] - 1;
            if(nums[i] > 0 && nums[i] < nums.length && nums[i] != nums[correct]){ //we should ignore negative numbers and number greater than length of array
                int temp = nums[i];
                nums[i] = nums[correct];
                nums[correct] = temp;
            } else{
                i ++;
            }
        }
        for(i = 0 ; i < nums.length ; i ++){
            if(nums[i] != i + 1){
                System.out.println(i + 1);
                return;
            }
        }
        System.out.println(nums.length + 1);
    }
}
