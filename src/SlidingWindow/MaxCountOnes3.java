package SlidingWindow;

public class MaxCountOnes3 {
    //1004
    //https://leetcode.com/problems/max-consecutive-ones-iii/description/
    /**
     * Given a binary array nums and an integer k, return the maximum number of consecutive 1's in the array if you can flip at most k 0's.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
     * Output: 6
     * Explanation: [1,1,1,0,0,1,1,1,1,1,1]
     * Bolded numbers were flipped from 0 to 1. The longest subarray is underlined.
     * Example 2:
     *
     * Input: nums = [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1], k = 3
     * Output: 10
     * Explanation: [0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1]
     * Bolded numbers were flipped from 0 to 1. The longest subarray is underlined.
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 105
     * nums[i] is either 0 or 1.
     * 0 <= k <= nums.length
     */
    static void main() {
        int[] nums = {1,1,1,0,0,0,1,1,1,1,0};
        int k = 2;
        //Approach - we are going to find sub arrays with atmost k 0s and find max length among those sub arrays
        int l = 0;
        int r = 0;
        int max = 0;
        int zero = 0;
        while(r < nums.length){
            if(nums[r] == 0){//add counter if 0 comes
                zero ++;
            }
            if(zero > k){ //if it greater than k start traversion from l and change zero value
                if(nums[l] == 0){
                    zero --;
                }
                l ++;
            } else{ //else compare max
                int len = r - l + 1;
                max = Math.max(max, len);
            }
            r ++;
        }
        System.out.println(max);
    }
}
