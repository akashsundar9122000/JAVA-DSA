package SlidingWindow;

public class BinarySubarrayWithSum {
    //930
    //https://leetcode.com/problems/binary-subarrays-with-sum/description/
    /**
     * Given a binary array nums and an integer goal, return the number of non-empty subarrays with a sum goal.
     *
     * A subarray is a contiguous part of the array.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,0,1,0,1], goal = 2
     * Output: 4
     * Explanation: The 4 subarrays are bolded and underlined below:
     * [1,0,1,0,1]
     * [1,0,1,0,1]
     * [1,0,1,0,1]
     * [1,0,1,0,1]
     * Example 2:
     *
     * Input: nums = [0,0,0,0,0], goal = 0
     * Output: 15
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 3 * 104
     * nums[i] is either 0 or 1.
     * 0 <= goal <= nums.length
     */

    static void main() {
        int[] nums = {1,0,1,0,1};
        int goal = 2;
        int a = findSum(nums, goal); //first we find sub arrays with <= goal
        int b = findSum(nums, goal - 1); //then we find sub arrays with <= goal -1
        System.out.println(a - b); //finally subtracting it will give == goal answer.
    }

    static int findSum(int[] nums, int goal){
        if(goal < 0){
            return 0;
        }
        int l = 0;
        int r = 0;
        int sum = 0;
        int count = 0;
        while(r < nums.length){
            sum += nums[r];
            while(sum > goal){ //if sum > goal we need to move left pointer and subtract those sums.
                sum -= nums[l];
                l ++;
            }
            count += r - l + 1; //the entire length before the satisfied condition will be sub arrays that satisfies goals.
            r ++;
        }
        return count;
    }
}
