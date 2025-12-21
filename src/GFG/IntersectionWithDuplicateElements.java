package GFG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IntersectionWithDuplicateElements {
    //https://www.geeksforgeeks.org/batch/gfg-160-problems/track/hashing-gfg-160/problem/intersection-of-two-arrays-with-duplicate-elements
    //https://www.geeksforgeeks.org/dsa/intersection-of-two-arrays/
    /**
     * Given two integer arrays a[] and b[], you have to find the intersection of the two arrays. Intersection of two arrays is said to be elements that are common in both the arrays. The intersection should not have duplicate elements and the result should contain items in any order.
     *
     * Note: The driver code will sort the resulting array in increasing order before printing.
     *
     * Examples:
     *
     * Input: a[] = [1, 2, 1, 3, 1], b[] = [3, 1, 3, 4, 1]
     * Output: [1, 3]
     * Explanation: 1 and 3 are the only common elements and we need to print only one occurrence of common elements.
     * Input: a[] = [1, 1, 1], b[] = [1, 1, 1, 1, 1]
     * Output: [1]
     * Explanation: 1 is the only common element present in both the arrays.
     * Input: a[] = [1, 2, 3], b[] = [4, 5, 6]
     * Output: []
     * Explanation: No common element in both the arrays.
     * Constraints:
     * 1 ≤ a.size(), b.size() ≤ 105
     * 0 ≤ a[i], b[i] ≤ 105
     */

    static void main() {
        int[] a = {1, 2, 1, 3, 1};
        int[] b = {3, 1, 3, 4, 1};
        System.out.println(intersect(a, b));
    }

    static ArrayList<Integer> intersect(int[] a, int[] b) {
        // code here
        Set<Integer> res = new HashSet<>();
        for(int i =0 ; i < a.length ; i ++){
            res.add(a[i]);
        }
        ArrayList<Integer> ans = new ArrayList<>();
        for(int j = 0 ; j < b.length ; j ++){
            if(res.contains(b[j])){
                ans.add(b[j]);
                res.remove(b[j]);
            }
        }
        return ans;
    }
}
