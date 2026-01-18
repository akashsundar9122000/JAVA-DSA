package Graph;

public class IslandPerimeter {
    //463
    //https://leetcode.com/problems/island-perimeter/description/?envType=problem-list-v2&envId=depth-first-search
    /**
     * You are given row x col grid representing a map where grid[i][j] = 1 represents land and grid[i][j] = 0 represents water.
     *
     * Grid cells are connected horizontally/vertically (not diagonally). The grid is completely surrounded by water, and there is exactly one island (i.e., one or more connected land cells).
     *
     * The island doesn't have "lakes", meaning the water inside isn't connected to the water around the island. One cell is a square with side length 1. The grid is rectangular, width and height don't exceed 100. Determine the perimeter of the island.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: grid = [[0,1,0,0],[1,1,1,0],[0,1,0,0],[1,1,0,0]]
     * Output: 16
     * Explanation: The perimeter is the 16 yellow stripes in the image above.
     * Example 2:
     *
     * Input: grid = [[1]]
     * Output: 4
     * Example 3:
     *
     * Input: grid = [[1,0]]
     * Output: 4
     *
     *
     * Constraints:
     *
     * row == grid.length
     * col == grid[i].length
     * 1 <= row, col <= 100
     * grid[i][j] is 0 or 1.
     * There is exactly one island in grid.
     */

    static void main() {
        int[][] grid = {
                {0,1,0,0},
                {1,1,1,0},
                {0,1,0,0},
                {1,1,0,0}
        };
        System.out.println(islandPerimeter(grid));
    }

    static int islandPerimeter(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int[][] visited = new int[m][n];
        int perimeter = 0;
        for(int i = 0 ; i < m ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if(visited[i][j] == 0 && grid[i][j] == 1){
                    perimeter += dfs(visited, grid, i, j);
                }
            }
        }
        return perimeter;
    }

    static int dfs(int[][] visited, int[][] grid, int r, int c){
        // Base cases for DFS recursion
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] == 0) {
            return 1; // Water contributes 1 to perimeter
        }
        if (visited[r][c] == 1) {
            return 0; // Already visited land cell
        }

        // Mark cell as visited
        visited[r][c] = 1;

        // Explore neighbors (up, down, left, right)
        return (dfs(visited,grid, r + 1, c) +
                dfs(visited,grid, r - 1, c) +
                dfs(visited,grid, r, c + 1) +
                dfs(visited,grid, r, c - 1));
    }
}
