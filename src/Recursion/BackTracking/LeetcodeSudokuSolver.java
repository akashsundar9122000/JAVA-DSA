package Recursion.BackTracking;

public class LeetcodeSudokuSolver {
    //37
    //https://leetcode.com/problems/sudoku-solver/description/
    /**
     * Write a program to solve a Sudoku puzzle by filling the empty cells.
     *
     * A sudoku solution must satisfy all of the following rules:
     *
     * Each of the digits 1-9 must occur exactly once in each row.
     * Each of the digits 1-9 must occur exactly once in each column.
     * Each of the digits 1-9 must occur exactly once in each of the 9 3x3 sub-boxes of the grid.
     * The '.' character indicates empty cells.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: board = [{'5','3','.','.','7','.','.','.','.'},{'6','.','.','1','9','5','.','.','.'},{'.','9','8','.','.','.','.','6','.'},{'8','.','.','.','6','.','.','.','3'},{'4','.','.','8','.','3','.','.','1'},{'7','.','.','.','2','.','.','.','6'},{'.','6','.','.','.','.','2','8','.'},{'.','.','.','4','1','9','.','.','5'},{'.','.','.','.','8','.','.','7','9'}]
     * Output: [{'5','3','4','6','7','8','9','1','2'},{'6','7','2','1','9','5','3','4','8'},{'1','9','8','3','4','2','5','6','7'},{'8','5','9','7','6','1','4','2','3'},{'4','2','6','8','5','3','7','9','1'},{'7','1','3','9','2','4','8','5','6'},{'9','6','1','5','3','7','2','8','4'},{'2','8','7','4','1','9','6','3','5'},{'3','4','5','2','8','6','1','7','9'}]
     * Explanation: The input board is shown above and the only valid solution is shown below:
     *
     *
     *
     *
     * Constraints:
     *
     * board.length == 9
     * board[i].length == 9
     * board[i][j] is a digit or '.'.
     * It is guaranteed that the input board has only one solution.
     */

    static void main() {
        char[][] board = {
                {'5','3','.','.','7','.','.','.','.'},
                {'6','.','.','1','9','5','.','.','.'},
                {'.','9','8','.','.','.','.','6','.'},
                {'8','.','.','.','6','.','.','.','3'},
                {'4','.','.','8','.','3','.','.','1'},
                {'7','.','.','.','2','.','.','.','6'},
                {'.','6','.','.','.','.','2','8','.'},
                {'.','.','.','4','1','9','.','.','5'},
                {'.','.','.','.','8','.','.','7','9'}
        };
        int[][] ans = new int[9][9];
        for(int i = 0 ; i < 9 ; i ++){
            for(int j = 0 ; j < 9 ; j ++){
                if(board[i][j] == '.'){
                    ans[i][j] = 0;
                } else{
                    ans[i][j] = board[i][j] - '0';
                }
            }
        }
        solve(ans);
        for(int i = 0 ; i < 9 ; i ++){
            for(int j = 0 ; j < 9 ; j ++){
                board[i][j] = (char)(ans[i][j] + '0');
            }
        }
        for (int i = 0 ;i < 9 ; i ++){
            for(int j = 0 ; j < 9 ; j ++){
                System.out.print(board[i][j]  + " ");
            }
            System.out.println();
        }
    }

    static boolean solve(int[][] board){
        int n = board.length;
        int row = -1;
        int col = -1;
        boolean emptyLeft = true; //check for any empty spot available to fill the sudoku
        for (int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if (board[i][j] == 0){ //0 is the empty places in the board
                    row = i;
                    col = j;
                    emptyLeft = false; //empty found
                    break;
                }
            }
            if(!emptyLeft){// if we found the empty in this row then break the loop
                break;
            }
        }
        if(emptyLeft){
            return true; // if no empty left in sudoku then it is solved so return true
        }
        //backtrack
        for(int number = 1 ; number <= 9 ; number ++){ //numbers inside suduko is from 1 to 9
            if (isSafe(board, row, col, number)){
                board[row][col] = number;
                if(solve(board)){
                    //found the answer
                    return true;
                } else{
                    board[row][col] = 0;
                }
            }
        }
        return false;
    }

    static boolean isSafe(int[][] board, int row, int col, int num){
        //check the entire row and col
        for(int i = 0 ; i < board.length ; i ++){
            //check the number is already in that row in board
            if(board[row][i] == num){
                return false;
            }
            //check the number is already in that col in board
            if(board[i][col] == num){
                return false;
            }
        }
        //check inner squares
        int sqrt = (int) Math.sqrt(board.length); //for variable inputs like n = 5,...
        int rowStart = row - row % sqrt;
        int colStart = col - col % sqrt;
        for(int i = rowStart ; i < rowStart + sqrt ; i ++){
            for(int j = colStart ; j < colStart + sqrt ; j ++){
                if(board[i][j] == num){
                    return false;
                }
            }
        }
        return true;
    }
}
