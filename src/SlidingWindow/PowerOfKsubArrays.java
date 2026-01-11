package SlidingWindow;

import java.util.Arrays;

public class PowerOfKsubArrays {
    //3254, 3255
    //https://leetcode.com/problems/find-the-power-of-k-size-subarrays-i/?envType=problem-list-v2&envId=sliding-window
    //https://leetcode.com/problems/find-the-power-of-k-size-subarrays-ii/submissions/1881452719/?envType=problem-list-v2&envId=sliding-window
    /**
     * You are given an array of integers nums of length n and a positive integer k.
     *
     * The power of an array is defined as:
     *
     * Its maximum element if all of its elements are consecutive and sorted in ascending order.
     * -1 otherwise.
     * You need to find the power of all subarrays of nums of size k.
     *
     * Return an integer array results of size n - k + 1, where results[i] is the power of nums[i..(i + k - 1)].
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,2,3,4,3,2,5], k = 3
     *
     * Output: [3,4,-1,-1,-1]
     *
     * Explanation:
     *
     * There are 5 subarrays of nums of size 3:
     *
     * [1, 2, 3] with the maximum element 3.
     * [2, 3, 4] with the maximum element 4.
     * [3, 4, 3] whose elements are not consecutive.
     * [4, 3, 2] whose elements are not sorted.
     * [3, 2, 5] whose elements are not consecutive.
     * Example 2:
     *
     * Input: nums = [2,2,2,2,2], k = 4
     *
     * Output: [-1,-1]
     *
     * Example 3:
     *
     * Input: nums = [3,2,3,2,3,2], k = 2
     *
     * Output: [-1,3,-1,3,-1]
     *
     *
     *
     * Constraints:
     *
     * 1 <= n == nums.length <= 500
     * 1 <= nums[i] <= 105
     * 1 <= k <= n
     */

    static void main() {
        int[] nums = {1,2,3,4,3,2,5};
        int k = 3;
        int[] arr = new int[nums.length - k + 1];
        Arrays.fill(arr, -1);
        int index = 0;
        for(int i = 0 ; i < nums.length - k + 1 ; i ++){
            boolean found = true;
            for(int j = i; j < i + k - 1; j ++){
                if(nums[j] + 1 != nums[j + 1]){
                    found = false;
                    break;
                }
            }
            if(found){
                arr[index] = nums[i + k - 1];
            }
            index ++;
        }
        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.toString(slidingWindow(nums, k)));
    }

    static int[] slidingWindow(int[] nums, int k){
        if(k == 1){
            return nums;
        }
        int[] arr = new int[nums.length - k + 1];
        int r = 0;
        int l = 0;
        int count = 1;
        while(r < nums.length){
            if(r > 0 && nums[r] - 1 == nums[r - 1]){
                count ++;
            }
            if(r - l + 1 == k){//if length of subarray == window size
                if(count == k){//if the count == size then its valid
                    arr[l] = nums[r]; //give nums[r] to arr because it holds the max value
                } else{
                    arr[l] = -1;
                }
                if(l < r && nums[l] + 1 == nums[l + 1]){ //reduce the window and count
                    count --;
                }
                l ++;
            }
            r ++;
        }
        return arr;
    }
}
