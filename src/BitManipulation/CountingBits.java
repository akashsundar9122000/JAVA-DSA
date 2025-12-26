package BitManipulation;

import java.util.Arrays;

public class CountingBits {
    //338
    //https://leetcode.com/problems/counting-bits/description/?envType=problem-list-v2&envId=bit-manipulation
    /**
     * Given an integer n, return an array ans of length n + 1 such that for each i (0 <= i <= n), ans[i] is the number of 1's in the binary representation of i.
     *
     *
     *
     * Example 1:
     *
     * Input: n = 2
     * Output: [0,1,1]
     * Explanation:
     * 0 --> 0
     * 1 --> 1
     * 2 --> 10
     * Example 2:
     *
     * Input: n = 5
     * Output: [0,1,1,2,1,2]
     * Explanation:
     * 0 --> 0
     * 1 --> 1
     * 2 --> 10
     * 3 --> 11
     * 4 --> 100
     * 5 --> 101
     *
     *
     * Constraints:
     *
     * 0 <= n <= 105
     */

    static void main() {
        int n = 5;
        System.out.println(Arrays.toString(countBits(n)));
    }

    static int[] countBits(int n) {
        int[] arr = new int[n + 1];
        for(int i = 0 ; i <= n ; i ++){
            int count = 0;
            int res = i;
            while(res > 0){
                int bit = res & 1;
                if(bit == 1){
                    count ++;
                }
                res = res >> 1;
            }
            arr[i] = count;
        }
        return arr;
    }
}
