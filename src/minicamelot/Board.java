/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import static java.lang.Math.abs; //remove import after done testing

/**
 *
 * @author Matthew Laikhram
 */
public class Board {
    
    //Creates a new game board with pieces in the initial position
    public Board() {
        mustCapture = false;
        board = new int[14][8];
        
        for (int row = 0; row < 14; ++row) {
            for (int col = 0; col < 8; ++col) {
                if ((row < 3 || row > 10) && (col == 0 || col == 7)) board[row][col] = -1;
                else if ((row < 2 || row > 11) && (col == 1 || col == 6)) board[row][col] = -1;
                else if ((row < 1 || row > 12) && (col == 2 || col == 5)) board[row][col] = -1;
                else if (row == 4 && col > 1 && col < 6) board[row][col] = Constants.WHITE;
                else if (row == 5 && col > 2 && col < 5) board[row][col] = Constants.WHITE;
                else if (row == 8 && col > 2 && col < 5) board[row][col] = Constants.BLACK;
                else if (row == 9 && col > 1 && col < 6) board[row][col] = Constants.BLACK;               
            }
        }
    }
    
    public void doMove(Move m) {
        try {
            //check to see if move contains a valid piece
            if (board[m.piece.y][m.piece.x] <= 0) return;
            
            //record the color of the current piece
            int pColor = board[m.piece.y][m.piece.x];
            
            Cor newPos = new Cor(m.piece.add(m.dir));

            //check for hopping out of bounds
            if (board[newPos.y][newPos.x] < 0) return;
            
            //if space is not occupied, move to it
            if (board[newPos.y][newPos.x] == 0) {
                board[m.piece.y][m.piece.x] = 0;
                board[newPos.y][newPos.x] = pColor;
            }
            
            //otherwise try moving one space beyond that point
            else {
                
                //record the piece in the adjacent tile
                int hopColor = board[newPos.y][newPos.x];
                
                //keep track of and increment the newPos
                Cor hopPos = new Cor(newPos);
                newPos = newPos.add(m.dir);
                
                //check for hopping out of bounds
                if (board[newPos.y][newPos.x] < 0) return;
                
                if (board[newPos.y][newPos.x] == 0) {
                    board[m.piece.y][m.piece.x] = 0;
                    board[newPos.y][newPos.x] = pColor;
                    
                    //check to see if it was a capturing move
                    if (hopColor != pColor) {
                        board[hopPos.y][hopPos.x] = 0;
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            
        }
    }
    
    public void print() {
        for (int row = 0; row < 14; ++row) {
            for (int col = 0; col < 8; ++col) {
                System.out.print(abs(board[row][col]) + " ");  
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public int[][] board; //array representation of the board
    private boolean mustCapture; //determines if the pieces must make a capturing move when calculating valid moves
    
    public static void main(String[] args) {
        Board board = new Board();
        board.print();
        for (int i = 0; i < 5; ++i) {
            board.doMove(new Move(new Cor(2, 4 + i), new Cor(Constants.S)));
            board.print();
        }
        board.doMove(new Move(new Cor(3, 4), new Cor(Constants.SE)));
        board.print();
    }
}
