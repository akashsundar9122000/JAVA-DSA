package Graph;

import java.util.*;

public class ZeroOneMatrix {
    //542
    //https://leetcode.com/problems/01-matrix/
    /**
     * Given an m x n binary matrix mat, return the distance of the nearest 0 for each cell.
     *
     * The distance between two cells sharing a common edge is 1.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: mat = [[0,0,0],[0,1,0],[0,0,0]]
     * Output: [[0,0,0],[0,1,0],[0,0,0]]
     * Example 2:
     *
     *
     * Input: mat = [[0,0,0],[0,1,0],[1,1,1]]
     * Output: [[0,0,0],[0,1,0],[1,2,1]]
     *
     *
     * Constraints:
     *
     * m == mat.length
     * n == mat[i].length
     * 1 <= m, n <= 104
     * 1 <= m * n <= 104
     * mat[i][j] is either 0 or 1.
     * There is at least one 0 in mat.
     *
     */

    static void main() {
        int[][] mat = {
                {0,0,0},
                {0,1,0},
                {1,1,1}
        };
        int[][] res = updateMatrix(mat);
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
    static int[][] updateMatrix(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;
        int[][] visited = new int[m][n];
        int[][] resArr = new int[m][n];
        Queue<Pair> queue = new LinkedList<>();
        for(int i = 0 ; i < m ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if(visited[i][j] == 0 && mat[i][j] == 0){
                    //get all the 0s data and go bfs to 1 so we can mark all the 1
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
                    //so while going next level increase the step count because for next level 1's distance from 0 will be steps+1
                    queue.offer(new Pair(newRow, newCol, steps + 1));
                }
            }
        }
        return resArr;
    }
}
