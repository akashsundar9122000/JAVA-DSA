package BinarySearch;

public class Squareroot {
    //leetcode - 69 - easy
    //https://leetcode.com/problems/sqrtx/description/
    //find sqrt of number using binary search
    /**
     * Given a non-negative integer x, return the square root of x rounded down to the nearest integer. The returned integer should be non-negative as well.
     *
     * You must not use any built-in exponent function or operator.
     *
     * For example, do not use pow(x, 0.5) in c++ or x ** 0.5 in python.
     *
     *
     * Example 1:
     *
     * Input: x = 4
     * Output: 2
     * Explanation: The square root of 4 is 2, so we return 2.
     * Example 2:
     *
     * Input: x = 8
     * Output: 2
     * Explanation: The square root of 8 is 2.82842..., and since we round it down to the nearest integer, 2 is returned.
     */

    static void main() {
        int num = 25;
        int start = 1;
        int end = num;
        while(start <= end){
            int mid = start + (end - start) / 2;
            if(mid > num / mid){
                end = mid - 1;
            } else if(mid < num / mid){
                start = mid + 1;
            } else{
                System.out.println(mid);
                return;
            }
        }
        System.out.println(end); //if its not a perfect sqrt then possible positive answer
    }
}
