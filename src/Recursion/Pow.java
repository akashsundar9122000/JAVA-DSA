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
        int a = n;
        if(n < 0){
            a = n * -1;
        }
        double ans = getPow(x, a, 1.0);
        if(n < 0){
            ans = 1 / ans;
        }
        System.out.println(ans);
    }

    static double getPow(double x, int n, double a){
        if(n == 0){
            return a;
        }
        return getPow(x, n - 1, a * x);
    }
}
