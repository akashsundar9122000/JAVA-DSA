package TwoPointer;

import java.util.Arrays;

public class MoveZero {
    //283
    //https://leetcode.com/problems/move-zeroes/?envType=problem-list-v2&envId=two-pointers
    /**
     * Given an integer array nums, move all 0's to the end of it while maintaining the relative order of the non-zero elements.
     *
     * Note that you must do this in-place without making a copy of the array.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [0,1,0,3,12]
     * Output: [1,3,12,0,0]
     * Example 2:
     *
     * Input: nums = [0]
     * Output: [0]
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 104
     * -231 <= nums[i] <= 231 - 1
     */

    static void main() {
        int[] nums = {0,1,0,3,12};
        int noOfZero = 0;
        for(int i = nums.length - 1; i >= 0 ; i --){
            if(nums[i] == 0){
                noOfZero ++;
                for(int j = i ; j < nums.length - noOfZero; j ++){
                    int temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(nums));

        //method 2

        int index = 0;
        for(int i = 0 ; i < nums.length ; i ++){
            if(nums[i] != 0){
                nums[index] = nums[i];
                index ++;
            }
        }
        for(int i = index ; i < nums.length ; i ++){
            nums[i] = 0;
        }
        System.out.println(Arrays.toString(nums));
    }
}
