package Graph;

import java.util.*;

public class FloodFill {
    //733
    //https://leetcode.com/problems/flood-fill/description/
    /**
     * You are given an image represented by an m x n grid of integers image, where image[i][j] represents the pixel value of the image. You are also given three integers sr, sc, and color. Your task is to perform a flood fill on the image starting from the pixel image[sr][sc].
     *
     * To perform a flood fill:
     *
     * Begin with the starting pixel and change its color to color.
     * Perform the same process for each pixel that is directly adjacent (pixels that share a side with the original pixel, either horizontally or vertically) and shares the same color as the starting pixel.
     * Keep repeating this process by checking neighboring pixels of the updated pixels and modifying their color if it matches the original color of the starting pixel.
     * The process stops when there are no more adjacent pixels of the original color to update.
     * Return the modified image after performing the flood fill.
     *
     *
     *
     * Example 1:
     *
     * Input: image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
     *
     * Output: [[2,2,2],[2,2,0],[2,0,1]]
     *
     * Explanation:
     *
     *
     *
     * From the center of the image with position (sr, sc) = (1, 1) (i.e., the red pixel), all pixels connected by a path of the same color as the starting pixel (i.e., the blue pixels) are colored with the new color.
     *
     * Note the bottom corner is not colored 2, because it is not horizontally or vertically connected to the starting pixel.
     *
     * Example 2:
     *
     * Input: image = [[0,0,0],[0,0,0]], sr = 0, sc = 0, color = 0
     *
     * Output: [[0,0,0],[0,0,0]]
     *
     * Explanation:
     *
     * The starting pixel is already colored with 0, which is the same as the target color. Therefore, no changes are made to the image.
     *
     *
     *
     * Constraints:
     *
     * m == image.length
     * n == image[i].length
     * 1 <= m, n <= 50
     * 0 <= image[i][j], color < 216
     * 0 <= sr < m
     * 0 <= sc < n
     */

    static void main() {
        int[][] image = {
                {1,1,1},
                {1,1,0},
                {1,0,1}
        };
        int sr = 1;
        int sc = 1;
        int color = 2;
        int[][] res = floodFill(image, sr, sc, color);
        for(int[] mat : res){
            for(int i: mat){
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    static int[][] floodFill(int[][] image, int sr, int sc, int color) {
        return dfsMethod(image, sr, sc, color); //1ms
        //return bfsMethod(image, sr, sc, color); //2ms
    }

    static int[][] dfsMethod(int[][] image, int sr, int sc, int color){
        int n = image.length;
        int m = image[0].length;
        int[][] visited = new int[n][m];
        int[][] resultArr = new int[n][m];
        for(int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < m ; j ++){
                resultArr[i][j] = image[i][j];
            }
        }
        int val = image[sr][sc];
        resultArr[sr][sc] = color;
        dfs(image, visited, resultArr, sr, sc, val, color);
        return resultArr;
    }

    static void dfs(int[][] image, int[][] visited, int[][] resultArr, int r, int c, int val, int color){
        visited[r][c] = 1;
        if(r > 0 && image[r-1][c] == val && visited[r-1][c] == 0){
            resultArr[r-1][c] = color;
            dfs(image, visited, resultArr, r-1, c, val, color);
        }
        if(r < image.length - 1 && image[r+1][c] == val && visited[r+1][c] == 0){
            resultArr[r+1][c] = color;
            dfs(image, visited, resultArr, r+1, c, val, color);
        }
        if(c > 0 && image[r][c - 1] == val && visited[r][c - 1] == 0){
            resultArr[r][c - 1] = color;
            dfs(image, visited, resultArr, r, c - 1, val, color);
        }
        if(c < image[0].length -1  && image[r][c + 1] == val && visited[r][c + 1] == 0){
            resultArr[r][c + 1] = color;
            dfs(image, visited, resultArr, r, c + 1, val, color);
        }
    }

    static int[][] bfsMethod(int[][] image, int sr, int sc, int color){
        int n = image.length;
        int m = image[0].length;
        int[][] visited = new int[n][m];
        int[][] resultArr = new int[n][m];
        for(int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < m ; j ++){
                resultArr[i][j] = image[i][j];
            }
        }
        int val = image[sr][sc];
        resultArr[sr][sc] = color;
        bfs(image, visited, resultArr, sr, sc, val, color);
        return resultArr;
    }

    static void bfs(int[][] grid, int[][] visited, int[][] resultArr, int row, int col, int val, int color){
        Queue<List<Integer>> queue = new LinkedList<>();
        List<Integer> valArr = new ArrayList<>();
        //add the initial indices to queue
        valArr.add(row);
        valArr.add(col);
        queue.offer(valArr);
        //mark visited
        visited[row][col] = 1;
        while(!queue.isEmpty()){
            //process queue
            List<Integer> land = queue.poll();
            int r = land.get(0);
            int c = land.get(1);
            List<Integer> newLand;
            //add neighbouring lands and mark visited
            if(r > 0 && grid[r-1][c] == val && visited[r-1][c] != 1){
                newLand = new ArrayList<>();
                newLand.add(r-1);
                newLand.add(c);
                queue.offer(newLand);
                visited[r-1][c] = 1;
                resultArr[r-1][c] = color;
            }
            if(r < grid.length - 1 && grid[r + 1][c] == val && visited[r + 1][c] != 1){
                newLand = new ArrayList<>();
                newLand.add(r+1);
                newLand.add(c);
                queue.offer(newLand);
                visited[r+1][c] = 1;
                resultArr[r+1][c] = color;
            }
            if(c > 0 && grid[r][c-1] == val && visited[r][c-1] != 1){
                newLand = new ArrayList<>();
                newLand.add(r);
                newLand.add(c-1);
                queue.offer(newLand);
                visited[r][c-1] = 1;
                resultArr[r][c-1]=color;
            }
            if(c < grid[0].length - 1 && grid[r][c + 1] == val && visited[r][c+1] != 1){
                newLand = new ArrayList<>();
                newLand.add(r);
                newLand.add(c + 1);
                queue.offer(newLand);
                visited[r][c + 1] = 1;
                resultArr[r][c+1]=color;
            }
        }
    }
}
