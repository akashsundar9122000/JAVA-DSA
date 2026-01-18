package Graph;

import java.util.LinkedList;
import java.util.Queue;

public class SurroundingRegions {
    //130
    //https://leetcode.com/problems/surrounded-regions/description/
    /**
     * You are given an m x n matrix board containing letters 'X' and 'O', capture regions that are surrounded:
     *
     * Connect: A cell is connected to adjacent cells horizontally or vertically.
     * Region: To form a region connect every 'O' cell.
     * Surround: The region is surrounded with 'X' cells if you can connect the region with 'X' cells and none of the region cells are on the edge of the board.
     * To capture a surrounded region, replace all 'O's with 'X's in-place within the original board. You do not need to return anything.
     *
     *
     *
     * Example 1:
     *
     * Input: board = [['X','X','X','X'],['X','O','O','X'],['X','X','O','X'],['X','O','X','X']]
     *
     * Output: [['X','X','X','X'],['X','X','X','X'],['X','X','X','X'],['X','O','X','X']]
     *
     * Explanation:
     *
     *
     * In the above diagram, the bottom region is not captured because it is on the edge of the board and cannot be surrounded.
     *
     * Example 2:
     *
     * Input: board = [['X']]
     *
     * Output: [['X']]
     *
     *
     *
     * Constraints:
     *
     * m == board.length
     * n == board[i].length
     * 1 <= m, n <= 200
     * board[i][j] is 'X' or 'O'.
     */

    static void main() {
        //approach - traverse the boundary O's and mark it cant be converted to X, then obviousky the rest of the O's will be converted to X
        char[][] board = {
                {'X','X','X','X'},
                {'X','O','O','X'},
                {'X','X','O','X'},
                {'X','O','X','X'}
        };
        solve(board);
        for(char[] mat: board){
            for(char i : mat){
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    static void solve(char[][] board) {
        dfsMethod(board); //2ms
        bfsMethod(board); //3ms
    }

    static void dfsMethod(char[][] board){
        //approach - traverse the boundary O's and mark it cant be converted to X, then obviousky the rest of the O's will be converted to X
        int m = board.length;
        int n = board[0].length;
        int[][] visited = new int[m][n];
        //only process the border elements. Check for O. if the path with border contains O that path wont be correct path because it cant be surrounded by X
        for(int i = 0 ; i < m ; i ++){
            //first col
            if(board[i][0] == 'O' && visited[i][0] == 0){
                dfs(board, visited, i, 0);
            }
            //last col
            if(board[i][n - 1] == 'O' && visited[i][n - 1] == 0){
                dfs(board, visited, i, n - 1);
            }
        }
        for(int i = 0 ; i < n ; i ++){
            //first row
            if(board[0][i] == 'O' && visited[0][i] == 0){
                dfs(board, visited, 0, i);
            }
            //last row
            if(board[m - 1][i] == 'O' && visited[m-1][i] == 0){
                dfs(board, visited, m-1, i);
            }
        }
        for(int i = 0 ;i < m ; i ++){
            for(int j = 0 ; j < n ; j ++){
                //middle Os changed to X
                if(visited[i][j] == 0 && board[i][j] == 'O'){
                    board[i][j] = 'X';
                }
            }
        }
    }

    static void dfs(char[][] board, int[][] visited, int r, int c){
        visited[r][c] = 1;
        int[][] actions = {{1,0},{-1,0},{0,1},{0,-1}};
        for(int[] act : actions){
            int newRow = r + act[0];
            int newCol = c + act[1];
            if(newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length){
                //if the node is O and aslo attatched to borde O so it cant be changed to X. Mark it as visited
                if(board[newRow][newCol] == 'O' && visited[newRow][newCol] == 0){
                    visited[newRow][newCol] = 1;
                    dfs(board, visited, newRow, newCol);
                }
            }
        }
    }

    static void bfsMethod(char[][] board){
        int m = board.length;
        int n = board[0].length;
        int[][] visited = new int[m][n];
        //only process the board elements. Check for O. if the path with border contains O that path wont be correct path because it cant be surrounded by X
        for(int i = 0 ; i < m ; i ++){
            //first col
            if(board[i][0] == 'O' && visited[i][0] == 0){
                bfs(board, visited, i, 0);
            }
            //last col
            if(board[i][n - 1] == 'O' && visited[i][n - 1] == 0){
                bfs(board, visited, i, n - 1);
            }
        }
        for(int i = 0 ; i < n ; i ++){
            //first row
            if(board[0][i] == 'O' && visited[0][i] == 0){
                bfs(board, visited, 0, i);
            }
            //last row
            if(board[m - 1][i] == 'O' && visited[m-1][i] == 0){
                bfs(board, visited, m-1, i);
            }
        }
        for(int i = 0 ;i < m ; i ++){
            for(int j = 0 ; j < n ; j ++){
                //middle Os changed to X
                if(visited[i][j] == 0 && board[i][j] == 'O'){
                    board[i][j] = 'X';
                }
            }
        }
    }

    static class Pair{
        int row;
        int col;
        public Pair(int row, int col){
            this.row = row;
            this.col = col;
        }
    }

    static void bfs(char[][] board, int[][] visited, int r, int c){
        Queue<Pair> queue = new LinkedList<>();
        queue.offer(new Pair(r, c));
        visited[r][c] = 1;
        int[][] actions = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty()){
            Pair path = queue.poll();
            int row = path.row;
            int col = path.col;
            for(int[] act : actions){
                int newRow = row + act[0];
                int newCol = col + act[1];
                if(newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
                    if (board[newRow][newCol] == 'O' && visited[newRow][newCol] == 0) {
                        visited[newRow][newCol] = 1;
                        queue.add(new Pair(newRow, newCol));
                    }
                }
            }
        }
    }
}
