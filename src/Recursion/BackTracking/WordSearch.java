package Recursion.BackTracking;

import java.util.Arrays;

public class WordSearch {
    //79
    //https://leetcode.com/problems/word-search/?envType=problem-list-v2&envId=backtracking
    /**
     * Given an m x n grid of characters board and a string word, return true if word exists in the grid.
     *
     * The word can be constructed from letters of sequentially adjacent cells, where adjacent cells are horizontally or vertically neighboring. The same letter cell may not be used more than once.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
     * Output: true
     * Example 2:
     *
     *
     * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "SEE"
     * Output: true
     * Example 3:
     *
     *
     * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCB"
     * Output: false
     *
     *
     * Constraints:
     *
     * m == board.length
     * n = board[i].length
     * 1 <= m, n <= 6
     * 1 <= word.length <= 15
     * board and word consists of only lowercase and uppercase English letters.
     */

    static void main() {
        char[][] board = {{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}};
        String word = "SEE";
        boolean[][] visited = new boolean[board.length][board[0].length];
        boolean found = false;
        for(int i = 0 ; i < board.length ; i ++){
            for(int j = 0 ; j < board[0].length ; j ++){
                if(board[i][j] == word.charAt(0)){ //only searching path if char in board = first letter in word
                    found = backTracking(board, word, i, j, visited, 0); //0 is index for word
                    if(found){
                        System.out.println(true);
                        return;
                    }
                }
            }
        }
        System.out.println(false);
    }
    static boolean backTracking(char[][] board, String word, int row, int col, boolean[][] visited, int wordIndex){
        if(wordIndex == word.length()){ //word index is increasing in each recursive call that means if this condition becomes true the word is found
            return true;
        }
        if(row < 0 || row >= board.length || col < 0 || col >= board[0].length || visited[row][col] || board[row][col] != word.charAt(wordIndex)){ //if any of this condition satisfies then this recursive call dont have the word
            return false;
        }
        visited[row][col] = true; //marking the cells as visited to avoid search again in that particular recursive call
        if(backTracking(board, word, row + 1, col, visited, wordIndex + 1) ||//expanding search on all sides
                backTracking(board, word, row - 1, col, visited, wordIndex + 1) ||
                backTracking(board, word, row, col + 1, visited, wordIndex + 1) ||
                backTracking(board, word, row, col - 1, visited, wordIndex + 1)){
            return true;
        }
        visited[row][col] = false; //backtracking changing to old
        return false;
    }
}
