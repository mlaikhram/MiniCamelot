/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import static java.lang.Math.abs; //remove import after done testing
import java.util.LinkedList;

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
    
    //determines if cor value is in range of the board
    public boolean isValid(Cor loc) {
        if (loc.x < 0 || loc.x > 7 || loc.y < 0 || loc.y > 13 || board[loc.y][loc.x] == -1) {
            return false;
        }
        return true;
    }
    
    //applies a given move to the board, if the move is legal
    public void doMove(Move m) {
        //check to see if move contains a valid piece
        if (!isValid(m.piece) || board[m.piece.y][m.piece.x] == 0) return;

        //record the color of the current piece
        int pColor = board[m.piece.y][m.piece.x];

        Cor newPos = new Cor(m.piece.add(m.dir));

        //check for moving out of bounds
        if (!isValid(newPos)) return;

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
            if (!isValid(newPos)) return;

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
    
    //calculates the legal moves a given piece can do
    public LinkedList<Move> calcMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        //check to see if piece is valid
        if (!isValid(piece) || board[piece.y][piece.x] == 0) return ans;
        
        //if a capture move exists, then you must capture
        if (mustCapture) {
            ans.addAll(calcCaptureMoves(piece));
            return ans;
        }
        //otherwise you can do a plain move or canter move
        else {
            ans.addAll(calcPlainMoves(piece));
            ans.addAll(calcCanterMoves(piece));
            return ans;
        }
    }      
    
    //calculates the legal plain moves a piece can do
    private LinkedList<Move> calcPlainMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        //check to see if piece is valid
        if (!isValid(piece) || board[piece.y][piece.x] == 0) return ans;
        
        Cor dest;
        for (Cor dir : Constants.compass) {
            dest = piece.add(dir);
            if (isValid(dest) && board[dest.y][dest.x] == 0) {
                ans.add(new Move(piece, dir));
            }
        }  
        return ans;
    }
    
    //calculates the legal canter moves a piece can do
    private LinkedList<Move> calcCanterMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        return ans;
    }
    
    //calculates the legal capture moves a piece can do
    private LinkedList<Move> calcCaptureMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        return ans;
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
        LinkedList<Move> moves = board.calcMoves(new Cor(2, 4));
        System.out.println("Valid Moves: ");
        for (Move m : moves) {
            m.print();
        }
        /*
        for (int i = 0; i < 5; ++i) {
            board.doMove(new Move(new Cor(2, 4 + i), new Cor(Constants.S)));
            board.print();
        }
        board.doMove(new Move(new Cor(3, 4), new Cor(Constants.SE)));
        board.print();*/
    }
}
