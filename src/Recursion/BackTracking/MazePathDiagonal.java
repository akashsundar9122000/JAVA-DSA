package Recursion.BackTracking;

public class MazePathDiagonal {
    static void main() {
        printPathWithDiagonal("", 3, 3);
    }
    static void printPathWithDiagonal(String processed, int row, int col){
        if(row == 1 && col == 1){
            System.out.println(processed);
            return;
        }
        if(row > 1 && col > 1){
            printPathWithDiagonal(processed + 'D', row - 1, col - 1);
        }
        if(row > 1){
            printPathWithDiagonal(processed + 'V', row - 1 , col);
        }
        if(col > 1){
            printPathWithDiagonal(processed + 'H', row, col - 1);
        }
    }
}
