package SlidingWindow;

public class NoOfNiceSubArrays {
    //1248
    //https://leetcode.com/problems/count-number-of-nice-subarrays/description/
    /**
     * Given an array of integers nums and an integer k. A continuous subarray is called nice if there are k odd numbers on it.
     *
     * Return the number of nice sub-arrays.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,1,2,1,1], k = 3
     * Output: 2
     * Explanation: The only sub-arrays with 3 odd numbers are [1,1,2,1] and [1,2,1,1].
     * Example 2:
     *
     * Input: nums = [2,4,6], k = 1
     * Output: 0
     * Explanation: There are no odd numbers in the array.
     * Example 3:
     *
     * Input: nums = [2,2,2,1,2,2,1,2,2,2], k = 2
     * Output: 16
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 50000
     * 1 <= nums[i] <= 10^5
     * 1 <= k <= nums.length
     */

    static void main() {
        int[] nums = {2,2,2,1,2,2,1,2,2,2};
        int k = 2;
        //for below logic refer https://leetcode.com/problems/binary-subarrays-with-sum/description/
        int a = findSum(nums, k); //first we find sub arrays with <= goal
        int b = findSum(nums, k - 1); //then we find sub arrays with <= goal -1
        System.out.println(a - b); //finally subtracting it will give == goal answer.
    }
    static int findSum(int[] nums, int k){
        if(k < 0){
            return 0;
        }
        int r = 0;
        int l = 0;
        int sum = 0;
        int count = 0;
        while(r < nums.length){
            sum += (nums[r] % 2);
            while(sum > k){ //if sum > goal we need to move left pointer and subtract those sums.
                sum -= (nums[l] % 2);
                l ++;
            }
            count += r - l + 1; //the entire length before the satisfied condition will be sub arrays that satisfies goals.
            r ++;
        }
        return count;
    }
}
