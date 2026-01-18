package Graph;

import java.util.LinkedList;
import java.util.Queue;

public class NoOfEnclaves {
    //1020
    //https://leetcode.com/problems/number-of-enclaves/
    /**
     * You are given an m x n binary matrix grid, where 0 represents a sea cell and 1 represents a land cell.
     *
     * A move consists of walking from one land cell to another adjacent (4-directionally) land cell or walking off the boundary of the grid.
     *
     * Return the number of land cells in grid for which we cannot walk off the boundary of the grid in any number of moves.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: grid = [[0,0,0,0],[1,0,1,0],[0,1,1,0],[0,0,0,0]]
     * Output: 3
     * Explanation: There are three 1s that are enclosed by 0s, and one 1 that is not enclosed because its on the boundary.
     * Example 2:
     *
     *
     * Input: grid = [[0,1,1,0],[0,0,1,0],[0,0,1,0],[0,0,0,0]]
     * Output: 0
     * Explanation: All 1s are either on the boundary or can reach the boundary.
     *
     *
     * Constraints:
     *
     * m == grid.length
     * n == grid[i].length
     * 1 <= m, n <= 500
     * grid[i][j] is either 0 or 1.
     */

    static void main() {
        int[][] grid = {
                {0,0,0,0},
                {1,0,1,0},
                {0,1,1,0},
                {0,0,0,0}
        };
        System.out.println(numEnclaves(grid));
    }

    //approach - traverse the boundary which is 1 and mark it visited, then obviously the rest of the 1 with not visited is answer
    static int numEnclaves(int[][] grid) {
        //return dfsMethod(grid); //11ms
        return bfsMethod(grid); //10ms
    }

    static int dfsMethod(int[][] grid){
        int m = grid.length;
        int n = grid[0].length;
        //only process the border elements. Check for 1. if the path with border contains 1
        int[][] visited = new int[m][n];
        for(int i = 0 ; i < m ; i ++){
            //first col
            if(grid[i][0] == 1 && visited[i][0] == 0){
                dfs(grid, visited, i, 0);
            }
            //last col
            if(grid[i][n - 1] == 1 && visited[i][n-1] == 0){
                dfs(grid, visited, i, n - 1);
            }
        }

        for(int i = 0 ; i < n ; i ++){
            //first row
            if(grid[0][i] == 1 && visited[0][i] == 0){
                dfs(grid, visited, 0, i);
            }
            //last row
            if(grid[m - 1][i] == 1 && visited[m - 1][i] == 0){
                dfs(grid, visited, m - 1, i);
            }
        }
        int noOfLands = 0;
        for(int i = 0 ; i < m ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if(visited[i][j] == 0 && grid[i][j] == 1){
                    noOfLands ++;
                }
            }
        }
        return noOfLands;
    }

    static void dfs(int[][] grid, int[][] visited, int r, int c){
        visited[r][c] = 1;
        int[][] actions = {{1,0},{-1,0},{0,1},{0,-1}};
        for(int[] act : actions){
            int newRow = r + act[0];
            int newCol = c + act[1];
            if(newRow >= 0 && newRow < grid.length && newCol >= 0 && newCol < grid[0].length){
                //if the node is 1 and aslo attatched to border so it cant be land cant escape
                if(grid[newRow][newCol] == 1 && visited[newRow][newCol] == 0){
                    dfs(grid, visited, newRow, newCol);
                }
            }
        }
    }

    //bfs method

    public static class Pair{
        int row;
        int col;
        public Pair(int row, int col){
            this.row = row;
            this.col = col;
        }
    }

    static int bfsMethod(int[][] grid){
        int m = grid.length;
        int n = grid[0].length;
        //only process the border elements. Check for 1. if the path with border contains 1
        int[][] visited = new int[m][n];
        for(int i = 0 ; i < m ; i ++){
            //first col
            if(grid[i][0] == 1 && visited[i][0] == 0){
                bfs(grid, visited, i, 0);
            }
            //last col
            if(grid[i][n - 1] == 1 && visited[i][n-1] == 0){
                bfs(grid, visited, i, n - 1);
            }
        }

        for(int i = 0 ; i < n ; i ++){
            //first row
            if(grid[0][i] == 1 && visited[0][i] == 0){
                bfs(grid, visited, 0, i);
            }
            //last row
            if(grid[m - 1][i] == 1 && visited[m - 1][i] == 0){
                bfs(grid, visited, m - 1, i);
            }
        }
        int noOfLands = 0;
        for(int i = 0 ; i < m ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if(visited[i][j] == 0 && grid[i][j] == 1){
                    noOfLands ++;
                }
            }
        }
        return noOfLands;
    }

    static void bfs(int[][] grid, int[][] visited, int r, int c){
        Queue<Pair> queue = new LinkedList<>();
        queue.offer(new Pair(r, c));
        visited[r][c] = 1;
        while (!queue.isEmpty()){
            Pair land = queue.poll();
            int row = land.row;
            int col = land.col;
            int[][] actions = {{1,0},{-1,0},{0,1},{0,-1}};
            for(int[] act : actions){
                int newRow = row + act[0];
                int newCol = col+ act[1];
                if(newRow >= 0 && newRow < grid.length && newCol >= 0 && newCol < grid[0].length) {
                    if(grid[newRow][newCol] == 1 && visited[newRow][newCol] == 0){
                        visited[newRow][newCol] = 1;
                        queue.offer(new Pair(newRow, newCol));
                    }
                }
            }
        }
    }
}
