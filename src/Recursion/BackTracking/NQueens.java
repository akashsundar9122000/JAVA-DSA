package Recursion.BackTracking;

public class NQueens {
    //Print number of ways N queens can be placed in NXN matrix
    //52
    //https://leetcode.com/problems/n-queens-ii/description/

    /**
     * The n-queens puzzle is the problem of placing n queens on an n x n chessboard such that no two queens attack each other.
     *
     * Given an integer n, return the number of distinct solutions to the n-queens puzzle.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: n = 4
     * Output: 2
     * Explanation: There are two distinct solutions to the 4-queens puzzle as shown.
     * Example 2:
     *
     * Input: n = 1
     * Output: 1
     *
     *
     * Constraints:
     *
     * 1 <= n <= 9
     */
    static void main() {
        int n = 4;
        boolean[][] board = new boolean[n][n];
        System.out.println(quuens(board, 0)); //only passing row because we are going to start with col = 0 in each recursion
    }

    static int quuens(boolean[][] board, int row){
        if(row == board.length){ //base condition when all rows are proceesed all the queens are placed in board.
            display(board);
            System.out.println();
            return 1;
        }
        int count = 0;
        for(int col = 0 ; col < board.length ; col ++){ //scan through all cols for the row to place queen
            if(isSafeToPlace(board, row, col)){
                board[row][col] = true; //if its safe blocking that block of the board;
                count += quuens(board, row + 1); //searching the row below to place queen and adding it to count.
                board[row][col] = false; //backtracking making it as it was before this recursive call.
            }
        }
        return count;
    }

    static boolean isSafeToPlace(boolean[][] board, int row, int col){
        //check vertical row (from 0th index to the rowth index)
        for (int i = 0 ; i < row ; i ++){
            if(board[i][col]){
                return false;
            }
        }
        //diagonal left ->  min(row, col) -> so it will be decrease row by 1 and col by onein each iteration untill min(row, col)
        int maxLeft = Math.min(row, col);
        for(int i = 1; i <= maxLeft ; i++){
            if(board[row - i][col - i]){
                return false;
            }
        }
        //diagonal right -> min(row, board.length - col - 1) -> so it will be increasing col by one and decreasing row by 1
        int maxRight = Math.min(row, board.length - col - 1);
        for(int i = 1; i <= maxRight ; i++){
            if(board[row - i][col + i]){
                return false;
            }
        }
        return true;
    }

    static void display(boolean[][] board){
        for(boolean[] row : board){
            for(boolean element : row){
                if(element){
                    System.out.print("Q ");
                } else{
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
}
