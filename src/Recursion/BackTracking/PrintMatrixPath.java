package Recursion.BackTracking;

import java.util.ArrayList;
import java.util.Arrays;

public class PrintMatrixPath {
    //printing path representation in matrix with path
    static void main() {
        boolean[][] maze = {{true, true, true},{true, true, true}, {true, true, true}};
        int[][] printPath = {{0,0,0},{0,0,0},{0,0,0}};
        printPathWithObstacles("", maze, printPath, 0, 0, 1);
    }
    static void printPathWithObstacles(String path, boolean[][] maze, int[][] printPath, int row, int col, int steps){
        if(row == maze.length - 1 && col == maze[0].length - 1){
            System.out.println(path);
            printPath[row][col] = steps;
            for(int i = 0 ; i < printPath.length ; i ++){
                for(int j = 0 ; j < printPath.length ; j ++){
                    System.out.print(printPath[i][j] + " ");
                }
                System.out.println();
            }
            return;
        }
        if(!maze[row][col]){
            return;
        }
        maze[row][col] = false; //making the path processed as false for that particular recusrsive call
        printPath[row][col] = steps;
        if(row > 0){
            printPathWithObstacles(path + 'U', maze, printPath, row - 1, col, steps + 1);
        }
        if(col > 0){
            printPathWithObstacles(path + 'L', maze, printPath, row, col - 1, steps + 1);
        }
        if(row < maze.length - 1){
            printPathWithObstacles(path + 'D', maze, printPath, row + 1, col, steps + 1);
        }
        if(col < maze[0].length - 1){
            printPathWithObstacles(path + 'R', maze, printPath, row, col + 1, steps + 1);
        }
        //backtracking making to old state for the next recursive call
        maze[row][col] = true;
        printPath[row][col] = 0;
    }
}
