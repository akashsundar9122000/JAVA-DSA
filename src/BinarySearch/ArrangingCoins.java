package BinarySearch;

public class ArrangingCoins {
    //leetcode - 441
    //https://leetcode.com/problems/arranging-coins/description/
    /**
     * ou have n coins and you want to build a staircase with these coins. The staircase consists of k rows where the ith row has exactly i coins. The last row of the staircase may be incomplete.
     *
     * Given the integer n, return the number of complete rows of the staircase you will build.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: n = 5
     * Output: 2
     * Explanation: Because the 3rd row is incomplete, we return 2.
     * Example 2:
     *
     *
     * Input: n = 8
     * Output: 3
     * Explanation: Because the 4th row is incomplete, we return 3.
     */

    static void main() {
        long n = 8;
        long start = 1;
        long end = n;
        long ans = 0;
        while (start <= end){
            long mid = start + (end - start) / 2;
            long count = (mid * (mid + 1)) / 2;
            if(count <= n){
                ans = mid;
                start = mid + 1;
            } else{
                end = mid - 1;
            }
        }
        System.out.println(ans);
    }
}
