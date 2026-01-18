package Graph;

public class PathWithMaxGold {
    //1219
    //https://leetcode.com/problems/path-with-maximum-gold/
    /**
     * In a gold mine grid of size m x n, each cell in this mine has an integer representing the amount of gold in that cell, 0 if it is empty.
     *
     * Return the maximum amount of gold you can collect under the conditions:
     *
     * Every time you are located in a cell you will collect all the gold in that cell.
     * From your position, you can walk one step to the left, right, up, or down.
     * You can't visit the same cell more than once.
     * Never visit a cell with 0 gold.
     * You can start and stop collecting gold from any position in the grid that has some gold.
     *
     *
     * Example 1:
     *
     * Input: grid = [[0,6,0],[5,8,7],[0,9,0]]
     * Output: 24
     * Explanation:
     * [[0,6,0],
     *  [5,8,7],
     *  [0,9,0]]
     * Path to get the maximum gold, 9 -> 8 -> 7.
     * Example 2:
     *
     * Input: grid = [[1,0,7],[2,0,6],[3,4,5],[0,3,0],[9,0,20]]
     * Output: 28
     * Explanation:
     * [[1,0,7],
     *  [2,0,6],
     *  [3,4,5],
     *  [0,3,0],
     *  [9,0,20]]
     * Path to get the maximum gold, 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7.
     *
     *
     * Constraints:
     *
     * m == grid.length
     * n == grid[i].length
     * 1 <= m, n <= 15
     * 0 <= grid[i][j] <= 100
     * There are at most 25 cells containing gold.
     */
    static int res = 0;
    static void main() {
        int[][] grid = {
                {1,0,7},
                {2,0,6},
                {3,4,5},
                {0,3,0},
                {9,0,20}
        };
        System.out.println(getMaximumGold(grid));
    }
    static int getMaximumGold(int[][] grid) {
        int n = grid.length;
        int m = grid[0].length;
        int[][] visited = new int[n][m];
        for(int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < m ; j ++){
                //if grid[]i]j] != 0 start the dfs
                if(visited[i][j] == 0 && grid[i][j] != 0){
                    dfs(grid, visited, i, j, grid[i][j]);
                }
            }
        }
        return res;
    }

    static void dfs(int[][] grid, int[][] visited, int r, int c, int sum){
        //mark visited
        visited[r][c] = 1;
        if(r > 0 && grid[r-1][c] != 0 && visited[r-1][c] == 0){
            //go up add value
            sum += grid[r-1][c];
            dfs(grid, visited, r - 1, c, sum);
            //backtrack remove the added value of this path
            sum -= grid[r-1][c];
        }
        //on other directions
        if(r < grid.length - 1 && grid[r+1][c] != 0 && visited[r+1][c] == 0){
            sum += grid[r+1][c];
            dfs(grid, visited, r + 1, c, sum);
            sum -= grid[r+1][c];
        }
        if(c > 0 && grid[r][c - 1] != 0 && visited[r][c - 1] == 0){
            sum += grid[r][c-1];
            dfs(grid, visited, r, c - 1, sum);
            sum -= grid[r][c - 1];
        }
        if(c < grid[0].length - 1 && grid[r][c + 1] != 0 && visited[r][c + 1] == 0){
            sum += grid[r][c + 1];
            dfs(grid, visited, r, c + 1, sum);
            sum -= grid[r][c + 1];
        }
        //backtrack - make path as unvisited so it can be processed on next traversals
        visited[r][c] = 0;
        //find max
        res = Math.max(res, sum);
    }
}
