package SlidingWindow;

import java.util.Arrays;

public class MinDiffBetweenHighestAndLowest {
    //1984
    //https://leetcode.com/problems/minimum-difference-between-highest-and-lowest-of-k-scores/?envType=problem-list-v2&envId=sliding-window
    /**
     * You are given a 0-indexed integer array nums, where nums[i] represents the score of the ith student. You are also given an integer k.
     *
     * Pick the scores of any k students from the array so that the difference between the highest and the lowest of the k scores is minimized.
     *
     * Return the minimum possible difference.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [90], k = 1
     * Output: 0
     * Explanation: There is one way to pick score(s) of one student:
     * - [90]. The difference between the highest and lowest score is 90 - 90 = 0.
     * The minimum possible difference is 0.
     * Example 2:
     *
     * Input: nums = [9,4,1,7], k = 2
     * Output: 2
     * Explanation: There are six ways to pick score(s) of two students:
     * - [9,4,1,7]. The difference between the highest and lowest score is 9 - 4 = 5.
     * - [9,4,1,7]. The difference between the highest and lowest score is 9 - 1 = 8.
     * - [9,4,1,7]. The difference between the highest and lowest score is 9 - 7 = 2.
     * - [9,4,1,7]. The difference between the highest and lowest score is 4 - 1 = 3.
     * - [9,4,1,7]. The difference between the highest and lowest score is 7 - 4 = 3.
     * - [9,4,1,7]. The difference between the highest and lowest score is 7 - 1 = 6.
     * The minimum possible difference is 2.
     *
     *
     * Constraints:
     *
     * 1 <= k <= nums.length <= 1000
     * 0 <= nums[i] <= 105
     */

    static void main() {
        int[] nums = {87063,61094,44530,21297,95857,93551,9918};
        int k = 6;
        Arrays.sort(nums);
        int res = Integer.MAX_VALUE;
        int l = 0;
        int r = k - 1;
        while(r < nums.length){
            res = Math.min(nums[r ++] - nums[l ++], res);
        }
        System.out.println(res);
    }
}
