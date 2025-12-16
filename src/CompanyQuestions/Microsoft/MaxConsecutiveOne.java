package CompanyQuestions.Microsoft;

import java.util.Hashtable;

public class MaxConsecutiveOne {
    //Leetcode - 485
    //https://leetcode.com/problems/max-consecutive-ones/description/
    /**
     * Given a binary array nums, return the maximum number of consecutive 1's in the array.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,1,0,1,1,1]
     * Output: 3
     * Explanation: The first two digits or the last three digits are consecutive 1s. The maximum number of consecutive 1s is 3.
     * Example 2:
     *
     * Input: nums = [1,0,1,1,0,1]
     * Output: 2
     */

    static void main() {
        int[] nums = {1,1,0,1,1,1};
        int max = 0;
        int res = 0;
        for(int i = 0 ; i < nums.length ; i ++){
            if(nums[i] == 1){
                res ++;
            } else{
                if(res > max){
                    max = res;
                }
                res = 0;
            }
        }
        if(res > max){
            max = res;
        }
        System.out.println(max);
    }
}
