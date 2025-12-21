package GFG;

import java.util.HashMap;
import java.util.Map;

public class CountPairswithGivenSum {
    //https://www.geeksforgeeks.org/batch/gfg-160-problems/track/hashing-gfg-160/problem/count-pairs-with-given-sum--150253
    //https://www.geeksforgeeks.org/dsa/count-pairs-with-given-sum/
    /**
     * You are given an array arr[] and an integer target. You have to count all pairs in the array such that their sum is equal to the given target.
     *
     * Examples:
     *
     * Input: arr[] = [1, 5, 7, -1, 5], target = 6
     * Output: 3
     * Explanation: Pairs with sum 6 are (1, 5), (7, -1) and (1, 5).
     * Input: arr[] = [1, 1, 1, 1], target = 2
     * Output: 6
     * Explanation: Pairs with sum 2 are (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1).
     * Input: arr[] = [10, 12, 10, 15, -1], target = 125
     * Output: 0
     * Explanation: There is no pair with sum = target
     * Constraints:
     * 1 ≤ arr.size() ≤ 105
     * -104 ≤ arr[i] ≤ 104
     * 0 ≤ target ≤ 104
     */

    static void main() {
        int[] arr = {1, 5, 7, -1, 5};
        int target = 6;
        System.out.println(countPairs(arr, target));
    }

    static int countPairs(int arr[], int target) {
        // code here
        Map<Integer, Integer> res = new HashMap<>();
        int count = 0;
        for(int i = 0 ; i < arr.length ; i ++){
            if(res.containsKey(target - arr[i])){
                count += res.get(target - arr[i]);
            }
            res.put(arr[i], res.getOrDefault(arr[i],0) + 1);
        }
        return count;
    }
}
