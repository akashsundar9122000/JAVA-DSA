package Recursion.BackTracking;

public class NKnights {
    //place N knights in NXN matrix
    static void main() {
        int n = 4;
        boolean[][] board = new boolean[n][n];
        knights(board, 0, 0, n);
    }

    static void knights(boolean[][] board, int row, int col, int knights){
        if(knights == 0){ //when all knights are placed it will be 0 so we display board.
            display(board);
            System.out.println();
            return;
        }
        if(row == board.length - 1 && col == board.length){ //its in last row after last column nothing will be there sop return
            return;
        }
        if(col == board.length){ //if we reach end of the col on the row just increase row to search next row cells
            knights(board, row + 1, 0, knights);
            return;
        }
        if(isSafe(board, row, col)){ //if its safe to place knight place it in board and call next col and reduce a knight because it is placed now
            board[row][col] = true;
            knights(board, row, col + 1, knights - 1); //go to next col for search with 1 less knight because it is placed in this cell
            board[row][col] = false; //backtrack change to as it was after recursive call;
        }
        knights(board, row, col + 1, knights); //if its not safe just go to next col for search without reducing the knight because it is not placed yet.
    }

    static void display(boolean[][] board){
        for(boolean[] row : board){
            for(boolean element : row){
                if(element){
                    System.out.print("K ");
                } else{
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    static boolean isSafe(boolean[][] board, int row, int col){
        //L shape of knight check
        if(isValid(board, row - 2, col - 1)){
            if(board[row - 2][col - 1]) {
                return false;
            }
        }
        if(isValid(board, row - 2, col + 1)){
            if(board[row - 2][col + 1]) {
                return false;
            }
        }
        if(isValid(board, row - 1, col - 2)){
            if(board[row - 1][col - 2]) {
                return false;
            }
        }
        if(isValid(board, row - 1, col + 2)){
            if(board[row - 1][col + 2]) {
                return false;
            }
        }
        return true;
    }

    static boolean isValid(boolean[][] board, int row, int col){
        if(row >= 0 && row < board.length && col >= 0 && col < board.length){ //bound conditions to avoid arrayindexoutofbound exception
            return true;
        }
        return false;
    }

}
