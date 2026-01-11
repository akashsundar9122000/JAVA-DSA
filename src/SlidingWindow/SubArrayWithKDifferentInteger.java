package SlidingWindow;

import java.util.HashMap;
import java.util.Map;

public class SubArrayWithKDifferentInteger {
    //992
    //https://leetcode.com/problems/subarrays-with-k-different-integers/description/?envType=problem-list-v2&envId=sliding-window
    /**
     * Given an integer array nums and an integer k, return the number of good subarrays of nums.
     *
     * A good array is an array where the number of different integers in that array is exactly k.
     *
     * For example, [1,2,3,1,2] has 3 different integers: 1, 2, and 3.
     * A subarray is a contiguous part of an array.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,2,1,2,3], k = 2
     * Output: 7
     * Explanation: Subarrays formed with exactly 2 different integers: [1,2], [2,1], [1,2], [2,3], [1,2,1], [2,1,2], [1,2,1,2]
     * Example 2:
     *
     * Input: nums = [1,2,1,3,4], k = 3
     * Output: 3
     * Explanation: Subarrays formed with exactly 3 different integers: [1,2,1,3], [2,1,3], [1,3,4].
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 2 * 104
     * 1 <= nums[i], k <= nums.length
     */

    static void main() {
        int[] nums = {1,2,1,2,3};
        int k = 2;
        System.out.println(subArraySum(nums, k) - subArraySum(nums, k - 1));
    }

    static int subArraySum(int[] nums, int k){
        if(k < 0){
            return 0;
        }
        Map<Integer, Integer> res = new HashMap<>();
        int r = 0;
        int l = 0;
        int count = 0;
        while(r < nums.length){
            //we put count for nums in array using right pointer
            res.put(nums[r], res.getOrDefault(nums[r], 0) + 1);
            while(res.size() > k){ //if it exceeds k we start to reduce from left pointer.
                res.put(nums[l], res.get(nums[l]) - 1);
                if(res.get(nums[l]) == 0){//if a integer value in map is 0 we can remove it
                    res.remove(nums[l]);
                }
                l ++;
            }
            count += r - l + 1; // then the length of subarray is added to count because all the previous part also will be the answer.
            r ++;
        }
        return count;
    }
}
