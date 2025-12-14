package BinarySearch;

public class SortedMatrixLeetcode {
    //leetcode - 74
    //https://leetcode.com/problems/search-a-2d-matrix/submissions/1853164312/
    /**
     * You are given an m x n integer matrix matrix with the following two properties:
     *
     * Each row is sorted in non-decreasing order.
     * The first integer of each row is greater than the last integer of the previous row.
     * Given an integer target, return true if target is in matrix or false otherwise.
     *
     * You must write a solution in O(log(m * n)) time complexity.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
     * Output: true
     * Example 2:
     *
     *
     * Input: matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 13
     * Output: false
     */

    static void main() {
        int[][] matrix = {{1,3,5,7},{10,11,16,20},{23,30,34,60}};
        int target = 3;
        int rows = matrix.length;
        int cols = matrix[0].length;
        int start = 0;
        int end = rows * cols - 1; //we are taking it as single array so total size would be this
        while (start <= end){
            int mid = start + (end - start) / 2;
            int row = mid / cols; //to find row of the mid index.
            int col = mid % 2; //to find col of mid index
            if(matrix[row][col] == target){
                System.out.println(true);
                return;
            } else if (matrix[row][col] > target){
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }
        System.out.println(false);
    }
}
