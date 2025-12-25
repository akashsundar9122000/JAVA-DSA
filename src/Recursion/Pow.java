package Recursion;

public class Pow {
    //50
    //https://leetcode.com/problems/powx-n/?envType=problem-list-v2&envId=recursion
    /**
     * Implement pow(x, n), which calculates x raised to the power n (i.e., xn).
     *
     *
     *
     * Example 1:
     *
     * Input: x = 2.00000, n = 10
     * Output: 1024.00000
     * Example 2:
     *
     * Input: x = 2.10000, n = 3
     * Output: 9.26100
     * Example 3:
     *
     * Input: x = 2.00000, n = -2
     * Output: 0.25000
     * Explanation: 2-2 = 1/22 = 1/4 = 0.25
     *
     *
     * Constraints:
     *
     * -100.0 < x < 100.0
     * -231 <= n <= 231-1
     * n is an integer.
     * Either x is not zero or n > 0.
     * -104 <= xn <= 104
     */

    static void main() {
        double x = 2.10000;
        int n = -2;
        System.out.println(myPow(x, n));
    }

    static double myPow(double x, int n) {
        return getPow(x,(long)n);
    }

    static double getPow(double x, long n){
        if(n == 0){
            return 1;
        }
        if(n < 0){
            return 1 / getPow(x, -n);
        }
        if(n % 2 != 0)
            return x * getPow(x * x, (n - 1) / 2);
        return getPow(x * x, n / 2);
    }
}
