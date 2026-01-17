package Graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NoOfIslands {
    //200
    //https://leetcode.com/problems/number-of-islands/
    /**
     * Given an m x n 2D binary grid grid which represents a map of '1's (land) and '0's (water), return the number of islands.
     *
     * An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.
     *
     *
     *
     * Example 1:
     *
     * Input: grid = [
     *   ['1','1','1','1','0'],
     *   ['1','1','0','1','0'],
     *   ['1','1','0','0','0'],
     *   ['0','0','0','0','0']
     * ]
     * Output: 1
     * Example 2:
     *
     * Input: grid = [
     *   ['1','1','0','0','0'],
     *   ['1','1','0','0','0'],
     *   ['0','0','1','0','0'],
     *   ['0','0','0','1','1']
     * ]
     * Output: 3
     *
     *
     * Constraints:
     *
     * m == grid.length
     * n == grid[i].length
     * 1 <= m, n <= 300
     * grid[i][j] is '0' or '1'.
     */

    static void main() {
        char[][] grid = {
                {'1','1','1','1','0'},
                {'1','1','0','1','0'},
                {'1','1','0','0','0'},
                {'0','0','0','0','0'}
        };
        System.out.println(numIslands(grid));
    }

    static int numIslands(char[][] grid) {
        //return bfsMethod(grid); //8ms
        return dfsMethod(grid); //3ms
    }

    static int bfsMethod(char[][] grid){
        int rowLength = grid.length;
        int colLength = grid[0].length;
        //create visited matrix
        int[][] visited = new int[rowLength][colLength];
        int island = 0;
        for(int i = 0 ; i < rowLength ; i ++){
            for(int j = 0 ; j < colLength ; j ++){
                if(visited[i][j] == 0 && grid[i][j] == '1'){
                    //if 1 land is find start searching for the nearest lands
                    island ++;
                    bfs(grid, visited, i, j);
                }
            }
        }
        return island;
    }

    static void bfs(char[][] grid, int[][] visited, int row, int col){
        Queue<List<Integer>> queue = new LinkedList<>();
        List<Integer> val = new ArrayList<>();
        //add the initial indices to queue
        val.add(row);
        val.add(col);
        queue.offer(val);
        //mark visited
        visited[row][col] = 1;
        while(!queue.isEmpty()){
            //process queue
            List<Integer> land = queue.poll();
            int r = land.get(0);
            int c = land.get(1);
            List<Integer> newLand;
            //add neighbouring lands and mark visited
            if(r > 0 && grid[r-1][c] == '1' && visited[r-1][c] != 1){
                newLand = new ArrayList<>();
                newLand.add(r-1);
                newLand.add(c);
                queue.offer(newLand);
                visited[r-1][c] = 1;
            }
            if(r < grid.length - 1 && grid[r + 1][c] == '1' && visited[r + 1][c] != 1){
                newLand = new ArrayList<>();
                newLand.add(r+1);
                newLand.add(c);
                queue.offer(newLand);
                visited[r+1][c] = 1;
            }
            if(c > 0 && grid[r][c-1] == '1' && visited[r][c-1] != 1){
                newLand = new ArrayList<>();
                newLand.add(r);
                newLand.add(c-1);
                queue.offer(newLand);
                visited[r][c-1] = 1;
            }
            if(c < grid[0].length - 1 && grid[r][c + 1] == '1' && visited[r][c+1] != 1){
                newLand = new ArrayList<>();
                newLand.add(r);
                newLand.add(c + 1);
                queue.offer(newLand);
                visited[r][c + 1] = 1;
            }
        }
    }

    static int dfsMethod(char[][] grid){
        int rowLength = grid.length;
        int colLength = grid[0].length;
        //create visited matrix
        int[][] visited = new int[rowLength][colLength];
        int island = 0;
        for(int i = 0 ; i < rowLength ; i ++){
            for(int j = 0 ; j < colLength ; j ++){
                if(visited[i][j] == 0 && grid[i][j] == '1'){
                    //if 1 land is find start searching for the nearest lands
                    island ++;
                    dfs(grid, visited, i, j);
                }
            }
        }
        return island;
    }

    static void dfs(char[][] grid, int[][] visited, int r, int c){
        visited[r][c] = 1;
        //recursive calls for nearby lands
        if(r > 0 && grid[r-1][c] == '1' && visited[r-1][c] != 1){
            dfs(grid, visited, r-1, c);
        }
        if(r < grid.length - 1 && grid[r + 1][c] == '1' && visited[r + 1][c] != 1){
            dfs(grid, visited, r+1, c);
        }
        if(c > 0 && grid[r][c-1] == '1' && visited[r][c-1] != 1){
            dfs(grid, visited, r, c - 1);
        }
        if(c < grid[0].length - 1 && grid[r][c + 1] == '1' && visited[r][c+1] != 1){
            dfs(grid, visited, r, c + 1);
        }
    }
}
