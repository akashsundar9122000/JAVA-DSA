package Graph;

import java.util.LinkedList;
import java.util.Queue;

public class MapOfHeighestPeak {
    //1765
    //https://leetcode.com/problems/map-of-highest-peak/description/
    /**
     * ou are given an integer matrix isWater of size m x n that represents a map of land and water cells.
     *
     * If isWater[i][j] == 0, cell (i, j) is a land cell.
     * If isWater[i][j] == 1, cell (i, j) is a water cell.
     * You must assign each cell a height in a way that follows these rules:
     *
     * The height of each cell must be non-negative.
     * If the cell is a water cell, its height must be 0.
     * Any two adjacent cells must have an absolute height difference of at most 1. A cell is adjacent to another cell if the former is directly north, east, south, or west of the latter (i.e., their sides are touching).
     * Find an assignment of heights such that the maximum height in the matrix is maximized.
     *
     * Return an integer matrix height of size m x n where height[i][j] is cell (i, j)'s height. If there are multiple solutions, return any of them.
     *
     *
     *
     * Example 1:
     *
     *
     *
     * Input: isWater = [[0,1],[0,0]]
     * Output: [[1,0],[2,1]]
     * Explanation: The image shows the assigned heights of each cell.
     * The blue cell is the water cell, and the green cells are the land cells.
     * Example 2:
     *
     *
     *
     * Input: isWater = [[0,0,1],[1,0,0],[0,0,0]]
     * Output: [[1,1,0],[0,1,1],[1,2,2]]
     * Explanation: A height of 2 is the maximum possible height of any assignment.
     * Any height assignment that has a maximum height of 2 while still meeting the rules will also be accepted.
     *
     *
     * Constraints:
     *
     * m == isWater.length
     * n == isWater[i].length
     * 1 <= m, n <= 1000
     * isWater[i][j] is 0 or 1.
     * There is at least one water cell.
     */
    static void main() {
        int[][] mat = {
                {0,0,1},
                {1,0,0},
                {0,0,0}
        };
        int[][] res = highestPeak(mat);
        for(int[] i : res){
            for(int j : i){
                System.out.print(j + " ");
            }
            System.out.println();
        }
    }

    private static class Pair{
        public int row;
        public int col;
        public int steps;
        public Pair(int r, int c, int steps){
            this.row = r;
            this.col = c;
            this.steps = steps;
        }
    }
    static int[][] highestPeak(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;
        int[][] visited = new int[m][n];
        int[][] resArr = new int[m][n];
        Queue<Pair> queue = new LinkedList<>();
        for(int i = 0 ; i < m ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if(visited[i][j] == 0 && mat[i][j] == 1){
                    //get all the 1s data and go bfs to 0 so we can mark all the 0
                    queue.offer(new Pair(i,j,0)); //initially 0 steps
                    visited[i][j] = 1;
                }
            }
        }
        int[][] actions = {{1,0},{-1,0},{0,1},{0,-1}};
        while(!queue.isEmpty()){
            Pair data = queue.poll();
            int row = data.row;
            int col = data.col;
            int steps = data.steps;
            resArr[row][col] = steps;
            //go on 4 sides
            for(int[] act : actions){
                int newRow = row + act[0];
                int newCol = col + act[1];
                if(newRow >=0 && newRow<mat.length && newCol >= 0 && newCol < mat[0].length && visited[newRow][newCol] == 0){
                    visited[newRow][newCol] = 1;
                    //so while going next level increase the step count because for next level 0's distance from 0 will be steps+1
                    queue.offer(new Pair(newRow, newCol, steps + 1));
                }
            }
        }
        return resArr;
    }
}
