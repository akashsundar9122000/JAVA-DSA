package Graph;

import java.util.*;

public class RottenOranges {
    //994
    //https://leetcode.com/problems/rotting-oranges/description/
    /**
     * You are given an m x n grid where each cell can have one of three values:
     *
     * 0 representing an empty cell,
     * 1 representing a fresh orange, or
     * 2 representing a rotten orange.
     * Every minute, any fresh orange that is 4-directionally adjacent to a rotten orange becomes rotten.
     *
     * Return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: grid = [[2,1,1],[1,1,0],[0,1,1]]
     * Output: 4
     * Example 2:
     *
     * Input: grid = [[2,1,1],[0,1,1],[1,0,1]]
     * Output: -1
     * Explanation: The orange in the bottom left corner (row 2, column 0) is never rotten, because rotting only happens 4-directionally.
     * Example 3:
     *
     * Input: grid = [[0,2]]
     * Output: 0
     * Explanation: Since there are already no fresh oranges at minute 0, the answer is just 0.
     *
     *
     * Constraints:
     *
     * m == grid.length
     * n == grid[i].length
     * 1 <= m, n <= 10
     * grid[i][j] is 0, 1, or 2.
     */

    static void main() {
        int[][] grid ={
                {2,1,1},
                {1,1,0},
                {0,1,1}
        };
        System.out.println(orangesRotting(grid));
    }
    static int orangesRotting(int[][] grid) {
        //cant do in DFS because we want to move rotten oranges at every index of matrix at same time
        int n = grid.length;
        int m = grid[0].length;
        int[][] visited = new int[n][m];
        //queue is created here itself because if there are rotten oranges present in more than 1 places in initial matrix all of them need to processed at same time
        Queue<List<Integer>> queue = new LinkedList<>();
        for(int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < m ; j ++){
                if(grid[i][j] == 2){
                    List<Integer> val = new ArrayList<>();
                    //add the initial indices to queue
                    val.add(i);
                    val.add(j);
                    queue.offer(val);
                }
            }
        }
        int mins = bfs(visited, grid, queue);
        for(int[] mat : grid){
            for(int i : mat){
                if(i == 1){
                    return -1;
                }
            }
        }
        return mins;
    }

    static int bfs(int[][] visited, int[][] grid, Queue<List<Integer>> queue){
        int mins = 0;
        while(!queue.isEmpty()){
            int size = queue.size();
            boolean changed = false;
            //looped because for 1 min all rotten oranges spoil their adjacent side.
            for(int i = 0 ; i < size ; i++){
                //process queue
                List<Integer> land = queue.poll();
                int r = land.get(0);
                int c = land.get(1);
                //mark visited
                visited[r][c] = 1;
                List<Integer> newLand;
                //add neighbouring lands and mark visited
                if(r > 0 && grid[r-1][c] == 1 && visited[r-1][c] != 1){
                    newLand = new ArrayList<>();
                    newLand.add(r-1);
                    newLand.add(c);
                    queue.offer(newLand);
                    //marked visited
                    visited[r-1][c] = 1;
                    //made fresh orange as rotten
                    grid[r-1][c] = 2;
                    changed = true;
                }
                if(r < grid.length - 1 && grid[r + 1][c] == 1 && visited[r + 1][c] != 1){
                    newLand = new ArrayList<>();
                    newLand.add(r+1);
                    newLand.add(c);
                    queue.offer(newLand);
                    visited[r+1][c] = 1;
                    grid[r+1][c] = 2;
                    changed = true;
                }
                if(c > 0 && grid[r][c-1] == 1 && visited[r][c-1] != 1){
                    newLand = new ArrayList<>();
                    newLand.add(r);
                    newLand.add(c-1);
                    queue.offer(newLand);
                    visited[r][c-1] = 1;
                    grid[r][c-1]=2;
                    changed = true;
                }
                if(c < grid[0].length - 1 && grid[r][c + 1] == 1 && visited[r][c+1] != 1){
                    newLand = new ArrayList<>();
                    newLand.add(r);
                    newLand.add(c + 1);
                    queue.offer(newLand);
                    visited[r][c + 1] = 1;
                    grid[r][c+1]=2;
                    changed = true;
                }
            }
            //minutes added if only oranges are rotten others
            if(changed)
                mins ++;
        }
        return mins;
    }
}
