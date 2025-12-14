package Leetcode;

public class MinSumInRotatedSortedArray {
    //Leetcode - 154
    //https://leetcode.com/problems/find-minimum-in-rotated-sorted-array-ii/submissions/1852184023/
    /**
     * Suppose an array of length n sorted in ascending order is rotated between 1 and n times. For example, the array nums = [0,1,4,4,5,6,7] might become:
     *
     * [4,5,6,7,0,1,4] if it was rotated 4 times.
     * [0,1,4,4,5,6,7] if it was rotated 7 times.
     * Notice that rotating an array [a[0], a[1], a[2], ..., a[n-1]] 1 time results in the array [a[n-1], a[0], a[1], a[2], ..., a[n-2]].
     *
     * Given the sorted rotated array nums that may contain duplicates, return the minimum element of this array.
     *
     * You must decrease the overall operation steps as much as possible.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,3,5]
     * Output: 1
     * Example 2:
     *
     * Input: nums = [2,2,2,0,1]
     * Output: 0
     */

    /**
     * Tried Binary but 3 cases failed so used 2 pointer approach
     * 0 ms solution
     * Tried binary search snippet below
     * public int findPivotIndex(int[] arr){
     *     //     int start = 0;
     *     //     int end = arr.length - 1;
     *     //     while (start < end){
     *     //         int mid = start + (end - start) / 2;
     *     //         if(end > mid && arr[mid] > arr[mid + 1]){
     *     //             return mid;
     *     //         }
     *     //         if(start < mid && arr[mid] < arr[mid - 1]){
     *     //             return mid - 1;
     *     //         }
     *     //         if(arr[start] < arr[mid] || (arr[start] == arr[mid] && arr[mid] > arr[end])){
     *     //             start = mid + 1;
     *     //         }
     *     //         else if(arr[start] >= arr[mid]){
     *     //             end = mid - 1;
     *     //         }
     *     //         else{
     *     //             start = mid;
     *     //         }
     *     //     }
     *     //     return -1;
     *     // }
     *
     *     Will print arr[findPivotIndex(arr) + 1]
     */


    static void main() {
        int[] arr = {2,2,2,0,1};
        int i = 0;
        int j = arr.length - 1;
        while(i<j){
            if(arr[i] > arr[i + 1]){
                System.out.println(arr[i + 1]);
                return;
            }
            if(arr[j] < arr[j-1]){
                System.out.println(arr[j]);
                return;
            }
            i ++;
            j --;
        }
        System.out.println(arr[0]);
    }
}
