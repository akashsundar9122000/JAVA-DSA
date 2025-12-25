package PrefixSum;

public class FindPivotIndex {
    //Leetcode - 724
    /**
     * Given an array of integers nums, calculate the pivot index of this array.
     *
     * The pivot index is the index where the sum of all the numbers strictly to the left of the index is equal to the sum of all the numbers strictly to the index's right.
     *
     * If the index is on the left edge of the array, then the left sum is 0 because there are no elements to the left. This also applies to the right edge of the array.
     *
     * Return the leftmost pivot index. If no such index exists, return -1.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,7,3,6,5,6]
     * Output: 3
     * Explanation:
     * The pivot index is 3.
     * Left sum = nums[0] + nums[1] + nums[2] = 1 + 7 + 3 = 11
     * Right sum = nums[4] + nums[5] = 5 + 6 = 11
     * Example 2:
     *
     * Input: nums = [1,2,3]
     * Output: -1
     * Explanation:
     * There is no index that satisfies the conditions in the problem statement.
     * Example 3:
     *
     * Input: nums = [2,1,-1]
     * Output: 0
     * Explanation:
     * The pivot index is 0.
     * Left sum = 0 (no elements to the left of index 0)
     * Right sum = nums[1] + nums[2] = 1 + -1 = 0
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 104
     * -1000 <= nums[i] <= 1000
     */

    static void main() {
        int[] nums = {-1,-1,-1,1,1,1};
        System.out.println(pivotIndex(nums));
    }

    static int pivotIndex(int[] nums) {
        int[] arr = new int[nums.length];
        arr[0] = nums[0];
        for(int i = 1 ; i < nums.length ; i ++){
            arr[i] = arr[i - 1] + nums[i];
        }
        for(int i = 0 ; i < nums.length ; i ++){
            if(i == 0){
                if(arr[nums.length - 1] - arr[i] == 0){
                    return i;
                }
            } else if(i == nums.length - 1){
                if(arr[nums.length - 2] == 0){
                    return nums.length - 1;
                }
            } else{
                if(arr[i - 1] == arr[nums.length - 1] - arr[i]){
                    return i;
                }
            }
        }
        return -1;
    }
}
