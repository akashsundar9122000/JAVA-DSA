package Recursion;

public class PowOfTwo {
    //Leetcode - 231
    //https://leetcode.com/problems/power-of-two/?envType=problem-list-v2&envId=recursion
    /**
     * Given an integer n, return true if it is a power of two. Otherwise, return false.
     *
     * An integer n is a power of two, if there exists an integer x such that n == 2x.
     *
     *
     *
     * Example 1:
     *
     * Input: n = 1
     * Output: true
     * Explanation: 20 = 1
     * Example 2:
     *
     * Input: n = 16
     * Output: true
     * Explanation: 24 = 16
     * Example 3:
     *
     * Input: n = 3
     * Output: false
     *
     *
     * Constraints:
     *
     * -231 <= n <= 231 - 1
     */

    static void main() {
        int n = 5;
        if(n == 1){
            System.out.println(true);
            return;
        }
        if(n <= 0 || n % 2 != 0){
            System.out.println(false);
            return;
        }
        System.out.println(powOfTwo(n, 0));
    }

    static boolean powOfTwo(int n , int mod){
        if(n == 1){
            if(mod == 0){
                return true;
            } else{
                return false;
            }
        }
        return powOfTwo(n/2, n%2);
    }
}
