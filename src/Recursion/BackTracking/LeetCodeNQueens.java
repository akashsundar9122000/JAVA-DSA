package Recursion.BackTracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCodeNQueens {
    //51
    //https://leetcode.com/problems/n-queens/description/
    /**
     * The n-queens puzzle is the problem of placing n queens on an n x n chessboard such that no two queens attack each other.
     *
     * Given an integer n, return all distinct solutions to the n-queens puzzle. You may return the answer in any order.
     *
     * Each solution contains a distinct board configuration of the n-queens' placement, where 'Q' and '.' both indicate a queen and an empty space, respectively.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: n = 4
     * Output: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
     * Explanation: There exist two distinct solutions to the 4-queens puzzle as shown above
     * Example 2:
     *
     * Input: n = 1
     * Output: [["Q"]]
     *
     *
     * Constraints:
     *
     * 1 <= n <= 9
     */
    //Time complexity - O(N!)

    static void main() {
        int n = 4;
        System.out.println(solveNQueens(n));
    }

    static List<List<String>> solveNQueens(int n) {
        String[][] board = new String[n][n];
        for(String[] row: board){
            Arrays.fill(row,"."); //Filling intital board with all '.'
        }
        List<List<String>> ans = new ArrayList<>();
        return queenBackTrack(board, 0, ans);
    }

    static List<List<String>> queenBackTrack(String[][] board, int row, List<List<String>> ans){
        if(row == board.length){ //base condition that it we passed over all rows
            List<String> temp = new ArrayList<>();
            for(String[] boardRow : board){
                temp.add(String.join("",boardRow));
            }
            ans.add(temp); //if we passed over all rows we found a way where we can place N queens in board
        }
        for(int col = 0 ; col < board.length ; col ++){
            if(isSafe(board, row, col)){ //check if safe to place queen in current row for current col.
                board[row][col] = "Q"; //if safe mark as Q
                queenBackTrack(board, row + 1, ans);
                board[row][col] = "."; //backtrack make the path to '.' if this recursive call is not the answer
            }
        }
        return ans; //code will come here once all recursive calls are over
    }
    static boolean isSafe(String[][] board, int row, int col){
        //check vertical from 0 to row
        for(int i = 0 ; i < row ; i ++){
            if(board[i][col] == "Q"){
                return false;
            }
        }
        //check diagonal left from current row-1,col-1 to row - (min(row,col)), col - min(row,col)
        int maxLeft = Math.min(row, col);
        for(int i = 1; i <= maxLeft ; i ++){
            if(board[row - i][col - i] == "Q"){
                return false;
            }
        }
        //checking diagonal right from current row-1,col+1 to row - min(row, total-col-1), col - min(row, total-col-1)
        int maxRight = Math.min(row, board.length - col - 1);
        for(int i = 1 ; i <= maxRight ; i ++){
            if(board[row - i][col + i] == "Q"){
                return false;
            }
        }
        return true;
    }
}
