package Recursion.BackTracking;

public class Maze {
    static void main() {
        System.out.println(coutOfPaths(3, 3)); //you are at 0,0 position need to reach 4,4 position but u can move only right or down
    }

    static int coutOfPaths(int r, int c){
        if(r == 1 || c == 1){//if we are in final row or final col that will be 1 path
            return 1;
        }
        int left = coutOfPaths(r - 1, c);//moving downwards
        int right = coutOfPaths(r, c - 1);//moving rightside
        return left + right;
    }
}
