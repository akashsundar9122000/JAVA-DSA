package BinarySearch;

public class MountainPeakIndex {
    //Leetcode question - 852, 162
    // https://leetcode.com/problems/peak-index-in-a-mountain-array/description/
    //https://leetcode.com/problems/find-peak-element/submissions/1847537355/
    //input array like [0,1,2,3,2,1] - it will increase at first then decrease, we need to find the peak index in this case peak element is 3 and its index is 3
    //also called as biotonic array

    /**
     * ou are given an integer mountain array arr of length n where the values increase to a peak element and then decrease.
     *
     * Return the index of the peak element.
     *
     * Your task is to solve it in O(log(n)) time complexity.
     *
     *
     *
     * Example 1:
     *
     * Input: arr = [0,1,0]
     *
     * Output: 1
     *
     * Example 2:
     *
     * Input: arr = [0,2,1,0]
     *
     * Output: 1
     *
     * Example 3:
     *
     * Input: arr = [0,10,5,2]
     *
     * Output: 1
     */

    static void main() {
        int arr[] = {0,2,1,0};
        System.out.println(findPeak(arr));
    }
    static int findPeak(int[] arr){
        int start = 0;
        int end = arr.length - 1;
        while(start != end){ //in the end the start and end index both will face the peakest number
            int mid = start + (end - start) / 2;
            if(arr[mid] > arr[mid + 1]){ //if mid is > mid + 1 then we are in the descending side so we should move the end to mid
                end = mid;
            } else if(arr[mid] < arr[mid + 1]){ //if mid is < mid + 1 then we are in the ascending array we can move start to the next element of the mid.
                start = mid + 1;
            }
        }
        return start; //start and end gonna be same at the end so we can return any one.
    }
}
