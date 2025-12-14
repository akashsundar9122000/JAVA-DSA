package BinarySearch;

public class MatrixTarget {
    static void main() {
        int[][] matrix = {{1,2,3},{4,5,6},{7,8,9}};
        int target = 7;
        int[] ans = getRowAndColIndex(matrix, target);
        System.out.println(ans[0] + " " + ans[1]);
    }

    static int[] getRowAndColIndex(int[][] matrix, int target){
        int start = 0;
        int end = matrix.length - 1;
        while (start < matrix.length && end >= 0){
            if(matrix[start][end] == target){
                return new int[]{start, end};
            }
            else if(matrix[start][end] > target){
                end --;
            }
            else {
                start ++;
            }
        }
        return new int[] {-1,-1};
    }
}
