package Recursion.BackTracking;

public class PathWithObstacles {
    static void main() {
         boolean[][] maze = {{true, true, true},{true, false, true}, {true, true, true}};
         printPathWithObstacles("", maze, 0, 0);
    }

    static void printPathWithObstacles(String path, boolean[][] maze, int row, int col){
        if(row == maze.length - 1 && col == maze[0].length - 1){
            System.out.println(path);
            return;
        }
        if(!maze[row][col]){ //if we face obstacle then return
            return;
        }
        if(row < maze.length - 1){
            printPathWithObstacles(path + 'D', maze,row + 1, col); //D - down
        }
        if(col < maze[0].length - 1){
            printPathWithObstacles(path + 'R', maze, row, col + 1); //R - right
        }
    }
}
