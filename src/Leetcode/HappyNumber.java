package Leetcode;

import java.util.HashSet;
import java.util.Set;

public class HappyNumber {
    //202
    //https://leetcode.com/problems/happy-number/description/
    /**
     * Write an algorithm to determine if a number n is happy.
     *
     * A happy number is a number defined by the following process:
     *
     * Starting with any positive integer, replace the number by the sum of the squares of its digits.
     * Repeat the process until the number equals 1 (where it will stay), or it loops endlessly in a cycle which does not include 1.
     * Those numbers for which this process ends in 1 are happy.
     * Return true if n is a happy number, and false if not.
     *
     *
     *
     * Example 1:
     *
     * Input: n = 19
     * Output: true
     * Explanation:
     * 12 + 92 = 82
     * 82 + 22 = 68
     * 62 + 82 = 100
     * 12 + 02 + 02 = 1
     * Example 2:
     *
     * Input: n = 2
     * Output: false
     *
     *
     * Constraints:
     *
     * 1 <= n <= 231 - 1
     */

    static void main() {
        int n = 19;
        int metho = n;
        //method 2 - fast and slow pointer
        System.out.println(method2(metho));
        Set<Integer> res = new HashSet();
        while(!res.contains(n)){
            res.add(n);
            int sum = 0;
            while(n > 0){
                int mod = n % 10;
                sum += (mod * mod);
                n = n / 10;
            }
            if(sum == 1){
                System.out.println(true);
                return;
            }
            n = sum;
        }
        System.out.println(false);

    }

    static boolean method2(int n){
        int fast = n;
        int slow = n;
        do{
            slow = findSquare(slow);
            fast = findSquare((findSquare(fast)));
        }while(slow != fast);

        if(slow == 1){
            return true;
        }
        return false;
    }

    static int findSquare(int n){
        int ans = 0;
        while(n > 0){
            int d = n % 10;
            ans += d * d;
            n/=10;
        }
        return ans;
    }
}
