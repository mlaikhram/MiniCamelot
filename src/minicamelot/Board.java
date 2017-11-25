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
    
    
    public int[][] board;
    private boolean mustCapture;
    
    public static void main(String[] args) {
        Board board = new Board();
        for (int row = 0; row < 14; ++row) {
            for (int col = 0; col < 8; ++col) {
                System.out.print(abs(board.board[row][col]) + " ");  
            }
            System.out.println();
        }
    }
}
