package BinarySearch;

public class FindTargetInMountainArray {
    //leetcode question - 1095
    //https://leetcode.com/problems/find-in-mountain-array/description/
    /**
     * You may recall that an array arr is a mountain array if and only if:
     *
     * arr.length >= 3
     * There exists some i with 0 < i < arr.length - 1 such that:
     * arr[0] < arr[1] < ... < arr[i - 1] < arr[i]
     * arr[i] > arr[i + 1] > ... > arr[arr.length - 1]
     * Given a mountain array mountainArr, return the minimum index such that mountainArr.get(index) == target. If such an index does not exist, return -1.
     *
     * You cannot access the mountain array directly. You may only access the array using a MountainArray interface:
     *
     * MountainArray.get(k) returns the element of the array at index k (0-indexed).
     * MountainArray.length() returns the length of the array.
     * Submissions making more than 100 calls to MountainArray.get will be judged Wrong Answer. Also, any solutions that attempt to circumvent the judge will result in disqualification.
     * Example 1:
     *
     * Input: mountainArr = [1,2,3,4,5,3,1], target = 3
     * Output: 2
     * Explanation: 3 exists in the array, at index=2 and index=5. Return the minimum index, which is 2.
     * Example 2:
     *
     * Input: mountainArr = [0,1,2,4,2,1], target = 3
     * Output: -1
     * Explanation: 3 does not exist in the array, so we return -1.
     */
    //Mountain array is given as input and a target is given we need to find the smallest index of the target in the array
    //The approach is first we need to find the peakest element index from the array.
    //From that we need to do order agnostic binary search (i.e) first we should search in first half (ascending) array in the mountain array.
    //If the target is found that will be the result because mountain array wont have duplicates.
    //If the target is not found then we should search in next half (i.e) descending array.
    //If target is not found in that return -1.
    static void main() {
        int mountainArr[] = {0,1,2,4,2,1};
        int target = 3;
        int peakIndex = findPeakIndex(mountainArr);
        int resultIndex = findUsingBinarySearch(mountainArr, target, 0, peakIndex, true); //checking in the ascending array so boolean is passed.
        if(resultIndex == -1){ //if target not found in ascending array go for descensding
            resultIndex = findUsingBinarySearch(mountainArr, target, peakIndex, mountainArr.length - 1,false);
        }
        System.out.println(resultIndex);
    }

    static int findPeakIndex(int[] arr){
        int start = 0;
        int end = arr.length - 1;
        while (start != end){
            int mid = start + (end - start) / 2;
            if(arr[mid] > arr[mid + 1]){
                end = mid;
            } else if(arr[mid] < arr[mid + 1]){
                start = mid + 1;
            }
        }
        return start; //start and end both points to peak index so we can return anything.
    }

    static int findUsingBinarySearch(int[] arr, int target, int start, int end, boolean isAscending){
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(arr[mid] == target){
                return mid;
            } else if(arr[mid] < target){
                if(isAscending){
                    start = mid + 1;
                } else{
                    end = mid - 1;
                }
            } else{
                if(isAscending){
                    end = mid - 1;
                } else{
                    start = mid + 1;
                }
            }
        }
        return -1;
    }
}
