package SlidingWindow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LongesHarmoniousSubArr {
    //594
    //https://leetcode.com/problems/longest-harmonious-subsequence/description/?envType=problem-list-v2&envId=sliding-window
    /**
     * We define a harmonious array as an array where the difference between its maximum value and its minimum value is exactly 1.
     *
     * Given an integer array nums, return the length of its longest harmonious subsequence among all its possible subsequences.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,3,2,2,5,2,3,7]
     *
     * Output: 5
     *
     * Explanation:
     *
     * The longest harmonious subsequence is [3,2,2,2,3].
     *
     * Example 2:
     *
     * Input: nums = [1,2,3,4]
     *
     * Output: 2
     *
     * Explanation:
     *
     * The longest harmonious subsequences are [1,2], [2,3], and [3,4], all of which have a length of 2.
     *
     * Example 3:
     *
     * Input: nums = [1,1,1,1]
     *
     * Output: 0
     *
     * Explanation:
     *
     * No harmonic subsequence exists.
     *
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 2 * 104
     * -109 <= nums[i] <= 109
     */

    static void main() {
        int[] nums = {1,3,2,2,5,2,3,7};
        System.out.println(method1(nums));
        System.out.println(method2(nums));
    }

    static int method1(int[] nums){
        Arrays.sort(nums);
        int r = 0;
        int l = 0;
        int maxLen = 0;
        while(r < nums.length){
            while(nums[r] - nums[l] > 1){
                l ++;
            }
            if(nums[r] - nums[l] == 1){
                maxLen = Math.max(maxLen, r - l + 1);
            }
            r ++;
        }
        return maxLen;
    }

    static int method2(int[] nums){
         Map<Integer, Integer> res = new HashMap<>();
         for(int num: nums){
             res.put(num, res.getOrDefault(num, 0) + 1);
         }
         int maxLen = 0;
         for(int num : nums){
             if(res.containsKey(num + 1)){
                 int len = res.get(num) + res.get(num + 1);
                 maxLen = Math.max(len, maxLen);
             }
         }
         return maxLen;
    }
}
