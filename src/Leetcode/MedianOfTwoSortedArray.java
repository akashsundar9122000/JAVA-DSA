package Leetcode;

public class MedianOfTwoSortedArray {
    //4
    //https://leetcode.com/problems/median-of-two-sorted-arrays/description/
    /**
     * Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays.
     *
     * The overall run time complexity should be O(log (m+n)).
     *
     *
     *
     * Example 1:
     *
     * Input: nums1 = [1,3], nums2 = [2]
     * Output: 2.00000
     * Explanation: merged array = [1,2,3] and median is 2.
     * Example 2:
     *
     * Input: nums1 = [1,2], nums2 = [3,4]
     * Output: 2.50000
     * Explanation: merged array = [1,2,3,4] and median is (2 + 3) / 2 = 2.5.
     *
     *
     * Constraints:
     *
     * nums1.length == m
     * nums2.length == n
     * 0 <= m <= 1000
     * 0 <= n <= 1000
     * 1 <= m + n <= 2000
     * -106 <= nums1[i], nums2[i] <= 106
     */
    //another sol - https://leetcode.com/problems/median-of-two-sorted-arrays/solutions/6723070/video-divide-each-array-into-two-groups-2hgjq

    static void main() {
        int[] nums1 = {1,2};
        int[] nums2 = {3,4};
        System.out.println();
        System.out.printf("%.5f",findMedianSortedArrays(nums1, nums2)); //2ms
    }

    static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int[] mergeArr = new int[nums1.length + nums2.length];
        int index = 0;
        int i = 0;
        int j = 0;
        while(i < nums1.length && j < nums2.length){
            if(nums1[i] <= nums2[j]){
                mergeArr[index] = nums1[i];
                i ++;
            }
            else if(nums1[i] > nums2[j]){
                mergeArr[index] = nums2[j];
                j ++;
            } else{
                mergeArr[index] = nums1[i];
                i ++;
                j ++;
            }
            index ++;
        }
        while(i < nums1.length){
            mergeArr[index] = nums1[i];
            index ++;
            i ++;
        }
        while(j < nums2.length){
            mergeArr[index] = nums2[j];
            j ++;
            index ++;
        }
        if((mergeArr.length & 1) == 1){
            return (double)(mergeArr[mergeArr.length / 2]);
        }
        int x = (mergeArr[mergeArr.length / 2]) + (mergeArr[mergeArr.length / 2 - 1]);
        return (double) x / 2;
    }
}
