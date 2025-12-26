package BitManipulation;

public class ReverseBit {
    //190
    //https://leetcode.com/problems/reverse-bits/description/?envType=problem-list-v2&envId=bit-manipulation
    /**
     * Reverse bits of a given 32 bits signed integer.
     *
     *
     *
     * Example 1:
     *
     * Input: n = 43261596
     *
     * Output: 964176192
     *
     * Explanation:
     *
     * Integer	Binary
     * 43261596	00000010100101000001111010011100
     * 964176192	00111001011110000010100101000000
     * Example 2:
     *
     * Input: n = 2147483644
     *
     * Output: 1073741822
     *
     * Explanation:
     *
     * Integer	Binary
     * 2147483644	01111111111111111111111111111100
     * 1073741822	00111111111111111111111111111110
     *
     *
     * Constraints:
     *
     * 0 <= n <= 231 - 2
     * n is even.
     */

    static void main() {
        int n = 2147483644;
        System.out.println(reverseBits(n));
    }

    static int reverseBits(int n) {
        int ans = 0;
        for(int i = 0 ; i < 32 ; i ++){
            int bit = n & 1; //get the least significant bit.
            ans = (ans << 1) | bit; //move ans to 1 bit left and add the significant bit.
            n = n >> 1; //move the n by 1 bit to take next bit.
        }
        return ans;
    }
}
