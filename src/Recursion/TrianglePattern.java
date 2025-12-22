package Recursion;

public class TrianglePattern {
    static void main() {
        printTriangleStaight(5, 0);
    }

    static void printTriangleInverted(int row, int col){
        if(row == 0){
            return;
        }

        if(col < row){
            System.out.print("* ");
            printTriangleInverted(row, col + 1);
        }
        else {
            System.out.println();
            printTriangleInverted(row - 1, 0);
        }
    }

    static void printTriangleStaight(int row, int col){
        if(row == 0){
            return;
        }

        if(col < row){
            printTriangleStaight(row, col + 1);
            System.out.print("* ");
        }
        else {
            printTriangleStaight(row - 1, 0);
            System.out.println();
        }
    }
}
