package Sorting;

public class SetMismatch {
    //leetcode - 645
    //https://leetcode.com/problems/set-mismatch/description/
    /**
     * You have a set of integers s, which originally contains all the numbers from 1 to n. Unfortunately, due to some error, one of the numbers in s got duplicated to another number in the set, which results in repetition of one number and loss of another number.
     *
     * You are given an integer array nums representing the data status of this set after the error.
     *
     * Find the number that occurs twice and the number that is missing and return them in the form of an array.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,2,2,4]
     * Output: [2,3]
     * Example 2:
     *
     * Input: nums = [1,1]
     * Output: [1,2]
     */

    //cyclic sort coz 1 to N
    static void main() {
        int nums[] = {1,2,2,4};
        int i = 0;
        while(i < nums.length){
            int correct = nums[i]- 1;
            if(nums[i] != nums[correct]){
                int temp = nums[i];
                nums[i] = nums[correct];
                nums[correct] = temp;
            } else{
                i ++;
            }
        }
        for(i = 0 ; i < nums.length ; i ++){
            if(nums[i] != i + 1){
                System.out.println(nums[i] + " " + (i + 1));
                return;
            }
        }
    }
}
