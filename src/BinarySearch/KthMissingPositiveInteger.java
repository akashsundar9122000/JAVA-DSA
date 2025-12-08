package BinarySearch;

public class KthMissingPositiveInteger {
    //Leetcode - 1539
    //https://leetcode.com/problems/kth-missing-positive-number/description/
    /**
     * Given an array arr of positive integers sorted in a strictly increasing order, and an integer k.
     *
     * Return the kth positive integer that is missing from this array.
     *
     *
     *
     * Example 1:
     *
     * Input: arr = [2,3,4,7,11], k = 5
     * Output: 9
     * Explanation: The missing positive integers are [1,5,6,8,9,10,12,13,...]. The 5th missing positive integer is 9.
     * Example 2:
     *
     * Input: arr = [1,2,3,4], k = 2
     * Output: 6
     * Explanation: The missing positive integers are [5,6,7,...]. The 2nd missing positive integer is 6.
     */

    static void main() {
        int arr[] = {2,3,4,7,11};
        int k = 5;
        int start = 0;
        int end = arr.length - 1;
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(arr[mid] - mid - 1 < k){ //this is the formula for example the mid is 4 and we want to see what index is mid in this input its 2 but actually 4 need to be in index 3 so we know 1 element is less so we move start = mid + 1
                start = mid + 1;
            } else{ //if not we move end
                end = mid - 1;
            }
        }
        System.out.println(start + k);
    }
}
