package Recursion.BackTracking;

public class MazePrintingPath {
    static void main() {
        printMazePath("",30,30); //Need to track the path that is processed
    }
    static void printMazePath(String pathProcessed, int r, int c){
        if(r == 1 && c == 1){ //Base condition is exact target point which is r = 1 and c = 1
            System.out.println(pathProcessed);
            return;
        }
        if(r > 1) //only reduce row if its > 1 because the row or col cant go below 1 because 1 will be last row or last col
            printMazePath(pathProcessed + 'D', r - 1, c);
        if(c > 1)
            printMazePath(pathProcessed + 'R', r, c - 1);
    }
}
