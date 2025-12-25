package PrefixSum;

public class SumOfAllOddLengthSubArray {
    //Leetcode - 1588
    //https://leetcode.com/problems/sum-of-all-odd-length-subarrays/?envType=problem-list-v2&envId=prefix-sum
    /**
     * Given an array of positive integers arr, return the sum of all possible odd-length subarrays of arr.
     *
     * A subarray is a contiguous subsequence of the array.
     *
     *
     *
     * Example 1:
     *
     * Input: arr = [1,4,2,5,3]
     * Output: 58
     * Explanation: The odd-length subarrays of arr and their sums are:
     * [1] = 1
     * [4] = 4
     * [2] = 2
     * [5] = 5
     * [3] = 3
     * [1,4,2] = 7
     * [4,2,5] = 11
     * [2,5,3] = 10
     * [1,4,2,5,3] = 15
     * If we add all these together we get 1 + 4 + 2 + 5 + 3 + 7 + 11 + 10 + 15 = 58
     * Example 2:
     *
     * Input: arr = [1,2]
     * Output: 3
     * Explanation: There are only 2 subarrays of odd length, [1] and [2]. Their sum is 3.
     * Example 3:
     *
     * Input: arr = [10,11,12]
     * Output: 66
     *
     *
     * Constraints:
     *
     * 1 <= arr.length <= 100
     * 1 <= arr[i] <= 1000
     */

    static void main() {
        int[] arr = {1,4,2,5,3};
        System.out.println(sumOddLengthSubarrays(arr));
    }
    static int sumOddLengthSubarrays(int[] arr) {
        int[] nums = new int[arr.length];
        nums[0] = arr[0];
        for(int i = 1; i < arr.length ; i ++){
            nums[i] = nums[i - 1] + arr[i];
        }
        int sum = 0;
        int i = 0;
        while(i < arr.length){
            for(int j = 0 ; j < arr.length  - i; j ++){
                sum += nums[j + i];
                if(j - 1 >= 0)
                    sum -= nums[j - 1];
            }
            i+=2;
        }
        return sum;
    }
}
