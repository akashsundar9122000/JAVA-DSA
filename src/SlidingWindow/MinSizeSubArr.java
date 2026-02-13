package SlidingWindow;

public class MinSizeSubArr {
    //209
    //https://leetcode.com/problems/minimum-size-subarray-sum/?envType=problem-list-v2&envId=sliding-window
    /**
     * Given an array of positive integers nums and a positive integer target, return the minimal length of a subarray whose sum is greater than or equal to target. If there is no such subarray, return 0 instead.
     *
     *
     *
     * Example 1:
     *
     * Input: target = 7, nums = [2,3,1,2,4,3]
     * Output: 2
     * Explanation: The subarray [4,3] has the minimal length under the problem constraint.
     * Example 2:
     *
     * Input: target = 4, nums = [1,4,4]
     * Output: 1
     * Example 3:
     *
     * Input: target = 11, nums = [1,1,1,1,1,1,1,1]
     * Output: 0
     *
     *
     * Constraints:
     *
     * 1 <= target <= 109
     * 1 <= nums.length <= 105
     * 1 <= nums[i] <= 104
     */

    static void main() {
        int nums[] = {2,3,1,2,4,3};
        int target = 7;
        System.out.println(minSubArrayLen(target, nums));
    }

    static int minSubArrayLen(int target, int[] nums) {
        int l = 0;
        int r = 0;
        int size = Integer.MAX_VALUE;
        int sum = 0;
        while(r<nums.length){
            sum += nums[r];
            while(sum >= target){
                size = Math.min(r - l + 1, size);
                sum -= nums[l];
                l ++;
            }
            r ++;
        }
        return size == Integer.MAX_VALUE ? 0 : size;
    }
}
