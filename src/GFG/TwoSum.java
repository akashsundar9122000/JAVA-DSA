package GFG;

import java.util.HashSet;
import java.util.Set;

public class TwoSum {
    //https://www.geeksforgeeks.org/batch/gfg-160-problems/track/hashing-gfg-160/problem/key-pair5616
    /**
     * Given an array arr[] of integers and another integer target. Determine if there exist two distinct indices such that the sum of their elements is equal to the target.
     *
     * Examples:
     *
     * Input: arr[] = [0, -1, 2, -3, 1], target = -2
     * Output: true
     * Explanation: arr[3] + arr[4] = -3 + 1 = -2
     * Input: arr[] = [1, -2, 1, 0, 5], target = 0
     * Output: false
     * Explanation: None of the pair makes a sum of 0
     * Input: arr[] = [11], target = 11
     * Output: false
     * Explanation: No pair is possible as only one element is present in arr[]
     * Constraints:
     * 1 ≤ arr.size ≤ 105
     * -105 ≤ arr[i] ≤ 105
     * -2*105 ≤ target ≤ 2*105
     */

    static void main() {
        int[] arr = {0, -1, 2, -3, 1};
        int target = -2;
        System.out.println(twoSum(arr, target));
    }

    static boolean twoSum(int arr[], int target) {
        // code here
        Set<Integer> res = new HashSet<>();
        res.add(arr[0]);
        for(int i = 1 ; i < arr.length ; i ++){
            if(res.contains(target - arr[i])){
                return true;
            }
            res.add(arr[i]);
        }
        return false;
    }
}
