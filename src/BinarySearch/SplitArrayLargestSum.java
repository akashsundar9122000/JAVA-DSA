package BinarySearch;

public class SplitArrayLargestSum {
    //Leetcode problem - 410 - Google question
    //https://leetcode.com/problems/split-array-largest-sum/submissions/1849244452/?envType=problem-list-v2&envId=binary-search
    /**
     * Given an integer array nums and an integer k, split nums into k non-empty subarrays such that the largest sum of any subarray is minimized.
     *
     * Return the minimized largest sum of the split.
     *
     * A subarray is a contiguous part of the array.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [7,2,5,10,8], k = 2
     * Output: 18
     * Explanation: There are four ways to split nums into two subarrays.
     * The best way is to split it into [7,2,5] and [10,8], where the largest sum among the two subarrays is only 18.
     * Example 2:
     *
     * Input: nums = [1,2,3,4,5], k = 2
     * Output: 9
     * Explanation: There are four ways to split nums into two subarrays.
     * The best way is to split it into [1,2,3] and [4,5], where the largest sum among the two subarrays is only 9.
     */

    /**
     * We think we cant use binary search here because the array is not in sorted order
     *         The array can be splitted into M subarrays. What can be minimum value for M, m = 1.
     *         What can be maximum value for M, m = nums.length
     *         So first find the sum of above 2 cases
     *         if m = 1 then the answer is maximum number in array nums
     *         if m = nums.length then the answer is sum(nums)
     *         So the final answer lies inbetween these 2 answers
     *         Now we can use binary search to find the final result
     */

    static void main() {
        int arr[] = {7,2,5,10,8};
        int m = 2;
        int start = 0;
        int end = 0;
        for(int i = 0 ; i < arr.length ; i ++){
            start = Math.max(start, arr[i]); //largest possible value when m = 1
            end += arr[i]; //largest possible value when m = arr.length
        }
        //So the final answer will lie between start and end

        System.out.println(findLargestsum(arr,start, end, m));
    }

    static int findLargestsum(int arr[], int start, int end, int m){
        while (start != end){
            int mid = start + (end - start) / 2;
            int pieces = 1; //We know we will take subarrays and there will be atlease 1 subarray.
            int sum = 0;
            for(int nums : arr){
                if(sum + nums > mid){ //if the sum of subarray > mid then we should not proceed with that we should end that subarray and crrate a new subarray
                    pieces ++; //So we will get another subarray now.
                    sum = nums; //That number will be sum of new subarray because we wont process loop again.
                } else{
                    sum += nums; //else we can add numbers to the subarray
                }
            }
            if(pieces > m){ //if pieces > m then we should increase the mid value for that we should increase the start.
                start = mid + 1;
            } else{ // if its <= then we should reduce the end to get small mid value.
                end = mid;
            }
        }
        //When the loop ends the start and middle and end will be the same and that will be the answer.
        return start;
    }
}
