package BinarySearch;

public class SearchInsertPosition {
    //leetcode - 35
    //https://leetcode.com/problems/search-insert-position/description/
    /**
     * Given a sorted array of distinct integers and a target value, return the index if the target is found. If not, return the index where it would be if it were inserted in order.
     *
     * You must write an algorithm with O(log n) runtime complexity.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,3,5,6], target = 5
     * Output: 2
     * Example 2:
     *
     * Input: nums = [1,3,5,6], target = 2
     * Output: 1
     * Example 3:
     *
     * Input: nums = [1,3,5,6], target = 7
     * Output: 4
     */

    static void main() {
        int arr[] = {1,3,5,6};
        int target = 7;
        int start = 0;
        int end = arr.length - 1;
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(arr[mid] == target){
                System.out.println(mid);
                return;
            } else if(arr[mid] > target){
                end = mid - 1;
            } else{
                start = mid + 1;
            }
        }
        System.out.println(end + 1);
    }
}
