package Leetcode;

public class AddDigit {
    //258
    //https://leetcode.com/problems/add-digits/description/
    /**
     * Given an integer num, repeatedly add all its digits until the result has only one digit, and return it.
     *
     *
     *
     * Example 1:
     *
     * Input: num = 38
     * Output: 2
     * Explanation: The process is
     * 38 --> 3 + 8 --> 11
     * 11 --> 1 + 1 --> 2
     * Since 2 has only one digit, return it.
     * Example 2:
     *
     * Input: num = 0
     * Output: 0
     *
     *
     * Constraints:
     *
     * 0 <= num <= 231 - 1
     */

    static void main() {
        int num = 38;
        System.out.println(1 + (num - 1) % 9); //method 2
        int x = 0;
        while(num > 10){
            x = num;
            int sum= 0;
            while(x > 0){
                sum += (x% 10);
                x/=10;
            }
            num = sum;
        }
        System.out.println(num);
    }
}
