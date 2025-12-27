package Recursion.BackTracking;

public class AllPathsWithObstacles {
    //We can move in all 4 directions
    static void main() {
        boolean[][] maze = {{true, true, true},{true, true, true}, {true, true, true}};
        printPathWithObstacles("", maze, 0, 0);
    }

    static void printPathWithObstacles(String path, boolean[][] maze, int row, int col){
        if(row == maze.length - 1 && col == maze[0].length - 1){
            System.out.println(path);
            return;
        }
        if(!maze[row][col]){
            return;
        }
        maze[row][col] = false; //already visited path so its false
        if(row > 0){
            printPathWithObstacles(path + 'U', maze, row - 1, col); //going up
        }
        if(col > 0){
            printPathWithObstacles(path + 'L', maze, row, col - 1); //going left
        }
        if(row < maze.length - 1){
            printPathWithObstacles(path + 'D', maze, row + 1, col); //going down
        }
        if (col < maze[0].length - 1){
            printPathWithObstacles(path + 'R', maze, row, col + 1); //going right
        }
        //here is where a fn gets over
        //before fn getting removed from stack we are backtracking the  changed values to original value.
        maze[row][col] = true;
    }
}
