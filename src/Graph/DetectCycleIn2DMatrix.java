package Graph;

import java.util.*;

public class DetectCycleIn2DMatrix {
    //1559
    //https://leetcode.com/problems/detect-cycles-in-2d-grid/
    /**
     * Given a 2D array of characters grid of size m x n, you need to find if there exists any cycle consisting of the same value in grid.
     *
     * A cycle is a path of length 4 or more in the grid that starts and ends at the same cell. From a given cell, you can move to one of the cells adjacent to it - in one of the four directions (up, down, left, or right), if it has the same value of the current cell.
     *
     * Also, you cannot move to the cell that you visited in your last move. For example, the cycle (1, 1) -> (1, 2) -> (1, 1) is invalid because from (1, 2) we visited (1, 1) which was the last visited cell.
     *
     * Return true if any cycle of the same value exists in grid, otherwise, return false.
     *
     *
     *
     * Example 1:
     *
     *
     *
     * Input: grid = [['a','a','a','a'],['a','b','b','a'],['a','b','b','a'],['a','a','a','a']]
     * Output: true
     * Explanation: There are two valid cycles shown in different colors in the image below:
     *
     * Example 2:
     *
     *
     *
     * Input: grid = [['c','c','c','a'],['c','d','c','c'],['c','c','e','c'],['f','c','c','c']]
     * Output: true
     * Explanation: There is only one valid cycle highlighted in the image below:
     *
     * Example 3:
     *
     *
     *
     * Input: grid = [['a','b','b'],['b','z','b'],['b','b','a']]
     * Output: false
     *
     *
     * Constraints:
     *
     * m == grid.length
     * n == grid[i].length
     * 1 <= m, n <= 500
     * grid consists only of lowercase English letters.
     */

    static void main() {
        char[][] grid = {
                {'a','b','b'},
                {'b','z','b'},
                {'b','b','a'}
        };
        System.out.println(containsCycle(grid));
    }

    static boolean containsCycle(char[][] grid) {
        //return bfsMethod(grid); //34 ms
        return dfsMethod(grid); //19ms
    }

    static boolean bfsMethod(char[][] grid){
        int n = grid.length;
        int m = grid[0].length;
        int[][] visited = new int[n][m];
        for(int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < m ; j ++){
                if(visited[i][j] == 0){
                    //if not visited traverse path
                    boolean cycle = bfs(visited, grid, i, j, grid[i][j]);
                    if(cycle){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static boolean bfs(int[][] visited, char[][] grid, int row, int col, char val){
        int[] rowActions = {1,-1,0,0};
        int[] colActions = {0,0,1,-1};
        Queue<List<Integer>> queue = new LinkedList<>();
        List<Integer> valArr = new ArrayList<>();
        //initially for thr first node parents will be -1
        valArr.add(-1);//parent Row
        valArr.add(-1); //parent Col
        valArr.add(row);
        valArr.add(col);
        queue.offer(valArr);
        visited[row][col] = 1;
        while(!queue.isEmpty()){
            List<Integer> land = queue.poll();
            int r = land.get(2);
            int c = land.get(3);
            int pr = land.get(0);
            int pc = land.get(1);
            for(int i = 0 ; i < 4 ; i ++){
                //loop for 4 sides
                int newRow = r + rowActions[i];
                int newCol = c + colActions[i];
                if(newRow >=0 && newRow < grid.length && newCol >=0 && newCol < grid[0].length && grid[newRow][newCol] == val){
                    //if the new row is visited
                    if(visited[newRow][newCol] == 1){
                        //if parent row or col is != to new row or col this means already this path is visited by some other traversal of same level so cycle exists. the next node is visited but now by its parent
                        if(pr != newRow || pc != newCol){
                            return true;
                        }
                    } else{
                        visited[newRow][newCol] = 1;
                        List<Integer> newVals = new ArrayList<>();
                        newVals.add(r);
                        newVals.add(c);
                        newVals.add(newRow);
                        newVals.add(newCol);
                        queue.offer(newVals);
                    }
                }
            }
        }
        return false;
    }

    static boolean dfsMethod(char[][] grid){
        int n = grid.length;
        int m = grid[0].length;
        int[][] visited = new int[n][m];
        for(int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < m ; j ++){
                if(visited[i][j] == 0){
                    //if not visited traverse path
                    boolean cycle = dfs(visited, grid, i, j, grid[i][j], -1, -1);
                    if(cycle){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static int[][] actions = {{1,0},{-1,0},{0,1},{0,-1}};

    static boolean dfs(int[][] visited, char[][] grid,int r, int c, char val, int parentRow, int parentCol){
        visited[r][c] = 1;
        for(int[] act : actions){
            //on 4 sides +1 and -1
            int newRow = r + act[0];
            int newCol = c + act[1];
            if(newRow >=0 && newRow < grid.length && newCol >= 0 && newCol < grid[0].length){
                if(grid[newRow][newCol] == val){
                    //ifpath exists
                    if(visited[newRow][newCol] == 0){
                        //if not visited go for recursion traverse
                        if(dfs(visited, grid, newRow, newCol, val, r, c)){
                            return true;
                        }
                    } else{
                        //if path is already visited but the parent is not same as the visted nodes parent then cucle exists
                        if(newRow != parentRow || newCol != parentCol){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
