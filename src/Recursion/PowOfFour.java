package Recursion;

public class PowOfFour {
    //342
    //https://leetcode.com/problems/power-of-four/description/?envType=problem-list-v2&envId=recursion

    /**
     * Given an integer n, return true if it is a power of four. Otherwise, return false.
     *
     * An integer n is a power of four, if there exists an integer x such that n == 4x.
     *
     *
     *
     * Example 1:
     *
     * Input: n = 16
     * Output: true
     * Example 2:
     *
     * Input: n = 5
     * Output: false
     * Example 3:
     *
     * Input: n = 1
     * Output: true
     *
     *
     * Constraints:
     *
     * -231 <= n <= 231 - 1
     */

    static void main() {
        int n = 16;
        if(n == 1){
            System.out.println(true);
            return;
        }
        if(n <= 0 || n % 4 != 0){
            System.out.println(false);
        }
        System.out.println(powOfFour(n, 0));
    }

    static boolean powOfFour(int n, int mod){
        if(n == 1){
            if(mod == 0){
                return true;
            } else{
                return false;
            }
        }
        if(mod != 0){
            return false;
        }
        return powOfFour(n / 4, n % 4);
    }
}
