package SlidingWindow;

public class LongestSubArrayOf1AfterDeleting1Zero {
    //1493
    //https://leetcode.com/problems/longest-subarray-of-1s-after-deleting-one-element/description/
    /**
     * Given a binary array nums, you should delete one element from it.
     *
     * Return the size of the longest non-empty subarray containing only 1's in the resulting array. Return 0 if there is no such subarray.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,1,0,1]
     * Output: 3
     * Explanation: After deleting the number in position 2, [1,1,1] contains 3 numbers with value of 1's.
     * Example 2:
     *
     * Input: nums = [0,1,1,1,0,1,1,0,1]
     * Output: 5
     * Explanation: After deleting the number in position 4, [0,1,1,1,1,1,0,1] longest subarray with value of 1's is [1,1,1,1,1].
     * Example 3:
     *
     * Input: nums = [1,1,1]
     * Output: 2
     * Explanation: You must delete one element.
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 105
     * nums[i] is either 0 or 1.
     */

    static void main() {
        int[] nums = {1,1,0,1};
        int l = 0;
        int r = 0;
        int max = 0;
        int zero = 0;
        while(r < nums.length){
            if(nums[r] == 0){//add counter if 0 comes
                zero ++;
            }
            while(zero == 2){ //if it 2 then we need to reduce 1 zero
                if(nums[l] == 0){
                    zero --;
                }
                l ++;
            }
            int len = r - l + 1;
            max = Math.max(max, len);

            r ++;
        }
        System.out.println(max - 1);
    }
}
