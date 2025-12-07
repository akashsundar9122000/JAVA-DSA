package BinarySearch;

public class SumOfTwoNumbersEqualTarget {
    //leetcode - 167

    /**
     * Given a 1-indexed array of integers numbers that is already sorted in non-decreasing order, find two numbers such that they add up to a specific target number. Let these two numbers be numbers[index1] and numbers[index2] where 1 <= index1 < index2 <= numbers.length.
     *
     * Return the indices of the two numbers, index1 and index2, added by one as an integer array [index1, index2] of length 2.
     *
     * The tests are generated such that there is exactly one solution. You may not use the same element twice.
     *
     * Your solution must use only constant extra space.
     *
     *
     *
     * Example 1:
     *
     * Input: numbers = [2,7,11,15], target = 9
     * Output: [1,2]
     * Explanation: The sum of 2 and 7 is 9. Therefore, index1 = 1, index2 = 2. We return [1, 2].
     * Example 2:
     *
     * Input: numbers = [2,3,4], target = 6
     * Output: [1,3]
     * Explanation: The sum of 2 and 4 is 6. Therefore index1 = 1, index2 = 3. We return [1, 3].
     * Example 3:
     *
     * Input: numbers = [-1,0], target = -1
     * Output: [1,2]
     * Explanation: The sum of -1 and 0 is -1. Therefore index1 = 1, index2 = 2. We return [1, 2].
     */

    /**
     * we can process each index (i) and subtract that value from target and make that as findValue and do binarySearch for i+1 to end and find another value
     */

    static void main() {
        int arr[] = {3,24,50,79,88,150,345};
        int target = 200;
        for(int i = 0 ; i < arr.length ; i ++){
            int resIndex = findInBinarySearch(arr, target - arr[i], i + 1, arr.length - 1);
            if(resIndex != -1){
                System.out.println((i + 1) + " " + (resIndex + 1));
                return;
            }
        }
        System.out.println(-1 + " " + -1);
    }

    static int findInBinarySearch(int[] arr, int remainingSum, int start, int end){
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(arr[mid] == remainingSum){
                return mid;
            } else if (arr[mid] < remainingSum) {
                start = mid + 1;
            } else{
                end = mid - 1;
            }
        }
        return -1;
    }
}
