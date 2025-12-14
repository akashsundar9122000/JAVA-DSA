package BinarySearch;

public class SortedMatrixTarget {
    //Sorted matrix is nothing btu the arr[1][0] will be greater than arr[0][n-1]

    static void main() {
        int[][] matrix = {{1,2,3},{4,5,6},{7,8,9}};
        int target = 9;
        int[] find = search(matrix, target);
        System.out.println(find[0] + " " + find[1]);
    }

    static int[] search(int[][] matrix, int target){
        int rows = matrix.length;
        int cols = matrix[0].length; //coz there may also be only one row
        if(rows == 1){
            return binarySearch(matrix,0, 0, cols - 1, target);
        }
        int rowStart = 0;
        int rowEnd = rows - 1;
        int columnMid = cols / 2;
        //we want to eliminate rows until only 2 rows are remaining
        while (rowStart < (rowEnd - 1)){ //while runs untill there are only 2 rows remaining
            int mid = rowStart + (rowEnd - rowStart) / 2;
            if(matrix[mid][columnMid] == target){
                return new int[]{mid,columnMid};
            } else if (matrix[mid][columnMid] > target) {
                rowEnd = mid;
            } else {
                rowStart = mid;
            }
        }
        //check target in the mid column of the 2 rows
        if(matrix[rowStart][columnMid] == target){
            return new int[]{rowStart,columnMid};
        }
        if(matrix[rowStart + 1][columnMid] == target){ //only 2 rows remaining
            return new int[]{rowStart + 1, columnMid};
        }
        //now search in all the 4 halves
        if(target <= matrix[rowStart][columnMid - 1]){
            return binarySearch(matrix, rowStart, 0, columnMid - 1, target);
        }
        if(target >= matrix[rowStart][columnMid + 1] && target <= matrix[rowStart][cols - 1]){
            return binarySearch(matrix, rowStart, columnMid + 1, cols - 1, target);
        }
        if(target <= matrix[rowStart + 1][columnMid - 1]){
            return binarySearch(matrix, rowStart + 1, 0, columnMid - 1, target);
        } else {
            return binarySearch(matrix, rowStart + 1, columnMid + 1, cols - 1, target);
        }
    }

    static int[] binarySearch(int[][] matrix, int row, int start, int end, int target){
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(matrix[row][mid] == target){
                return new int[]{row, mid};
            } else if(matrix[row][mid] > target){
                end = mid - 1;
            } else{
                start = mid + 1;
            }
        }
        return new int[]{-1,-1};
    }
}
