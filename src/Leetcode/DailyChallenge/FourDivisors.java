package Leetcode.DailyChallenge;

public class FourDivisors {
    //4/1/26
    //1390
    //https://leetcode.com/problems/four-divisors/?envType=daily-question&envId=2026-01-04

    /**
     * Given an integer array nums, return the sum of divisors of the integers in that array that have exactly four divisors. If there is no such integer in the array, return 0.
     * <p>
     * <p>
     * <p>
     * Example 1:
     * <p>
     * Input: nums = [21,4,7]
     * Output: 32
     * Explanation:
     * 21 has 4 divisors: 1, 3, 7, 21
     * 4 has 3 divisors: 1, 2, 4
     * 7 has 2 divisors: 1, 7
     * The answer is the sum of divisors of 21 only.
     * Example 2:
     * <p>
     * Input: nums = [21,21]
     * Output: 64
     * Example 3:
     * <p>
     * Input: nums = [1,2,3,4,5]
     * Output: 0
     * <p>
     * <p>
     * Constraints:
     * <p>
     * 1 <= nums.length <= 104
     * 1 <= nums[i] <= 105
     */

    static void main() {
        int[] nums = {21, 4, 7};
        System.out.println(sumFourDivisors(nums));
    }

    static int sumFourDivisors(int[] nums) {
        int sum = 0;
        for(int n : nums){
            int count = 0;
            int currentSum = 0;
            for (int d = 1; d * d <= n; d++) {
                if (n % d == 0) {
                    // d is a divisor
                    count++;
                    currentSum += d;

                    // If d is not the square root, add its counterpart n/d
                    if (d * d != n) {
                        count++;
                        currentSum += n / d;
                    }
                }

                // Optimization: if we already have more than 4, stop
                if (count > 4) break;
            }
            if(count == 4){
                sum += currentSum;
            }
        }
        return sum;
    }
}

