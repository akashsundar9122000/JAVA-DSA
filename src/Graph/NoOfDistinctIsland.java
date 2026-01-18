package Graph;

import java.lang.management.LockInfo;
import java.util.*;

public class NoOfDistinctIsland {
    //https://www.geeksforgeeks.org/problems/number-of-distinct-islands/1
    /**
     * Given a boolean 2D matrix grid of size n * m. You have to find the number of distinct islands where a group of connected 1s (horizontally or vertically) forms an island. Two islands are considered to be distinct if and only if one island is not equal to another (not rotated or reflected).
     *
     * Example 1:
     *
     * Input:
     * grid[][] = [[1, 1, 0, 0, 0],
     *             [1, 1, 0, 0, 0],
     *             [0, 0, 0, 1, 1],
     *             [0, 0, 0, 1, 1]]
     * Output: 1
     * Explanation:
     * grid[][] = [[1, 1, 0, 0, 0],
     *             [1, 1, 0, 0, 0],
     *             [0, 0, 0, 1, 1],
     *             [0, 0, 0, 1, 1]]
     * Same colored islands are equal. We have 2 equal islands, so we have only 1 distinct island.
     *
     * Example 2:
     *
     * Input:
     * grid[][] = [[1, 1, 0, 1, 1],
     *             [1, 0, 0, 0, 0],
     *             [0, 0, 0, 0, 1],
     *             [1, 1, 0, 1, 1]]
     * Output: 3
     * Explanation:
     * grid[][] = [[1, 1, 0, 1, 1],
     *             [1, 0, 0, 0, 0],
     *             [0, 0, 0, 0, 1],
     *             [1, 1, 0, 1, 1]]
     * Same colored islands are equal.
     * We have 4 islands, but 2 of them
     * are equal, So we have 3 distinct islands.
     *
     * Your Task: You don't need to read or print anything. Your task is to complete the function countDistinctIslands() which takes the grid as an input parameter and returns the total number of distinct islands.
     *
     * Constraints:
     * 1 ≤ n, m ≤ 500
     * grid[i][j] == 0 or grid[i][j] == 1
     */

    static void main() {
        int[][] grid = {
                {1, 1, 0, 1, 1},
                {1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1},
                {1, 1, 0, 1, 1}
        };

        int m = grid.length;
        int n = grid[0].length;
        int[][] visited = new int[m][n];
        Set<String> distinctIslands = new HashSet<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1 && visited[i][j] == 0) {
                    List<String> shape = new ArrayList<>();
                    dfs(grid, visited, i, j, i, j, shape);
                    distinctIslands.add(String.join("|", shape));
                }
            }
        }
        System.out.println(distinctIslands.size());
    }

    static void dfs(int[][] grid, int[][] visited, int baseR, int baseC, int r, int c, List<String> shape) {

        visited[r][c] = 1;
        shape.add((r - baseR) + "," + (c - baseC));

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (nr >= 0 && nr < grid.length &&
                    nc >= 0 && nc < grid[0].length &&
                    visited[nr][nc] == 0 &&
                    grid[nr][nc] == 1) {
                dfs(grid, visited, baseR, baseC, nr, nc, shape);
            }
        }
    }
}
