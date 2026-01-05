package Leetcode.DailyChallenge;

public class MaximumMatrixSum {
    //5/1/26
    //1975
    //https://leetcode.com/problems/maximum-matrix-sum/description/?envType=daily-question&envId=2026-01-05
    /**
     * You are given an n x n integer matrix. You can do the following operation any number of times:
     *
     * Choose any two adjacent elements of matrix and multiply each of them by -1.
     * Two elements are considered adjacent if and only if they share a border.
     *
     * Your goal is to maximize the summation of the matrix's elements. Return the maximum sum of the matrix's elements using the operation mentioned above.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: matrix = [[1,-1],[-1,1]]
     * Output: 4
     * Explanation: We can follow the following steps to reach sum equals 4:
     * - Multiply the 2 elements in the first row by -1.
     * - Multiply the 2 elements in the first column by -1.
     * Example 2:
     *
     *
     * Input: matrix = [[1,2,3],[-1,-2,-3],[1,2,3]]
     * Output: 16
     * Explanation: We can follow the following step to reach sum equals 16:
     * - Multiply the 2 last elements in the second row by -1.
     *
     *
     * Constraints:
     *
     * n == matrix.length == matrix[i].length
     * 2 <= n <= 250
     * -105 <= matrix[i][j] <= 105
     */

    static void main() {
        int[][] matrix = {{1,2,3},{-1,-2,-3},{1,2,3}};
        System.out.println(maxMatrixSum(matrix));
    }

    static long maxMatrixSum(int[][] matrix) {
        /**
         Approach - Possible maximum value answer will be when all the numbers in matrix are positive.
         So if there are even number of negative numbers every numbers can be turned to positive so that will be the answer
         But if there is odd set of negative numbers then we should subtract only one negative number but that should be smallest number when it is changed to positive.
         We should subtrack 2 * min number because the min number will be already added to the total sum.
         */
        int min = Integer.MAX_VALUE;
        long total = 0;
        int neg = 0;
        for(int[] i : matrix){
            for(int j : i){
                total += Math.abs(j);
                if(j < 0){
                    neg ++;
                }
                min = Math.min(min, Math.abs(j));
            }
        }
        if(neg % 2 != 0){
            total -= (2 * min);
        }
        return total;
    }
}
