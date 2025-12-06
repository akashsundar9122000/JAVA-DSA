package BinarySearch;

public class BinarySearchRotateArray {
    //Leetcode - 33
    //https://leetcode.com/problems/search-in-rotated-sorted-array/description/
    //Asked in amazon and google
    /**
     * here is an integer array nums sorted in ascending order (with distinct values).
     *
     * Prior to being passed to your function, nums is possibly left rotated at an unknown index k (1 <= k < nums.length) such that the resulting array is [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]] (0-indexed). For example, [0,1,2,4,5,6,7] might be left rotated by 3 indices and become [4,5,6,7,0,1,2].
     *
     * Given the array nums after the possible rotation and an integer target, return the index of target if it is in nums, or -1 if it is not in nums.
     *
     * You must write an algorithm with O(log n) runtime complexity.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [4,5,6,7,0,1,2], target = 0
     * Output: 4
     * Example 2:
     *
     * Input: nums = [4,5,6,7,0,1,2], target = 3
     * Output: -1
     * Example 3:
     *
     * Input: nums = [1], target = 0
     * Output: -1
     */

    /**
     * The array will have pivot element, before numbers are in ascending order after numbers also will be in ascending order.
     * First we need to find that pivot index then we can do binary search separately 2 times for before and after arrays of the pivot
     * To find pivot element there are 4 cases.
     * Case 1 - if(arr[mid] > arr[mid + 1]) then arr[mid] is obviously pivot element
     * Case 2 - if(arr[mid] < arr[mid - 1]) then arr[mid - 1] is obviously pivot element.
     * Case 3 - if(arr[start] >= arr[mid]) then we don't need to check the numbers after arr[mid] because obviously it will be smaller numbers so it cant be mid in that case end = mid - 1
     * Case 4 - else - start = mid + 1
     * After finding the pivot index we can run binary search for both arrays separately and find the target's index.
     * This approach will not work if array have duplicate values
     */

    static void main() {
        int arr[] = {4,5,6,7,0,1,2};
        int target = 3;
        int pivotIndex = findPivotIndex(arr);
        int resIndex = getBinarySearchResult(arr, target, 0, pivotIndex);
        if(resIndex == -1){
            resIndex = getBinarySearchResult(arr, target, pivotIndex + 1, arr.length - 1);
        }
        System.out.println(resIndex);
    }

    static int findPivotIndex(int[] arr){
        int start = 0;
        int end = arr.length - 1;
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(end > mid  && arr[mid] > arr[mid + 1]){ //case 1
                return mid;
            }
            if(start < mid && arr[mid] < arr[mid - 1]){ //case 2
                return mid - 1;
            }
            if(arr[start] >= arr[mid]){//case 3
                end = mid - 1;
            } else{// case 4
                end = start + 1;
            }
        }
        return -1;
    }

    static int getBinarySearchResult(int[] arr, int target, int start, int end){
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(arr[mid] < target){
                start = mid + 1;
            } else if(arr[mid] > target){
                end = mid - 1;
            } else{
                return mid;
            }
        }
        return -1;
    }
}
