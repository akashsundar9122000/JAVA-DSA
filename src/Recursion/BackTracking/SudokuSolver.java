package Recursion.BackTracking;

public class SudokuSolver {
    static void main() {
        int[][] board =new int[][] {
            {3,0,6,5,0,8,4,0,0},
            {5,2,0,0,0,0,0,0,0},
            {0,8,7,0,0,0,0,3,1},
            {0,0,3,0,1,0,0,8,0},
            {9,0,0,8,6,3,0,0,5},
            {0,5,0,0,9,0,6,0,0},
            {1,3,0,0,0,0,2,5,0},
            {0,0,0,0,0,0,0,7,4},
            {0,0,5,2,0,6,3,0,0}
        };
        if(solve(board)){
            display(board);
        }
        else {
            System.out.println("cannot solve");
        }
    }

    static boolean solve(int[][] board){
        int n = board.length;
        int row = -1;
        int col = -1;
        boolean emptyLeft = true; //check for any empty spot available to fill the sudoku
        for (int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if (board[i][j] == 0){ //0 is the empty places in the board
                    row = i;
                    col = j;
                    emptyLeft = false; //empty found
                    break;
                }
            }
            if(!emptyLeft){// if we found the empty in this row then break the loop
                break;
            }
        }
        if(emptyLeft){
            return true; // if no empty left in sudoku then it is solved so return true
        }
        //backtrack
        for(int number = 1 ; number <= 9 ; number ++){ //numbers inside suduko is from 1 to 9
            if (isSafe(board, row, col, number)){
                board[row][col] = number;
                if(solve(board)){
                    //found the answer
                    return true;
                } else{
                    board[row][col] = 0;
                }
            }
        }
        return false;
    }

    static void display(int[][] board){
        for(int[] row: board){
            for(int num : row){
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    static boolean isSafe(int[][] board, int row, int col, int num){
        //check the entire row and col
        for(int i = 0 ; i < board.length ; i ++){
            //check the number is already in that row in board
            if(board[row][i] == num){
                return false;
            }
            //check the number is already in that col in board
            if(board[i][col] == num){
                return false;
            }
        }
        //check inner squares
        int sqrt = (int) Math.sqrt(board.length); //for variable inputs like n = 5,...
        int rowStart = row - row % sqrt;
        int colStart = col - col % sqrt;
        for(int i = rowStart ; i < rowStart + sqrt ; i ++){
            for(int j = colStart ; j < colStart + sqrt ; j ++){
                if(board[i][j] == num){
                    return false;
                }
            }
        }
        return true;
    }
}
