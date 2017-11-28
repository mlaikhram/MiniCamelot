/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.LinkedList;

/**
 *
 * @author Matthew Laikhram
 */
public class GameNode {
    
    public GameNode(Board b, boolean ismax) {
        board = new Board(b);
        isMax = ismax;
        pieces = new LinkedList<>();
        
        //determine what colored piece to look at based on min or max
        int color;
        if (isMax) {
            color = Constants.BLACK;
        }
        else {
            color = Constants.WHITE;
        }
        
        //find all appropriate pieces on the board
        //pieces are added in order from top middle to bottom borders
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = (Constants.COLS - 1) / 2; col >= 0; --col) {
                if (board.get(row, col) == color) {
                    pieces.add(new Cor(col, row));
                }
                else if (board.get(row, Constants.COLS - 1 - col) == color) {
                    pieces.add(new Cor(Constants.COLS - 1 - col, row));
                }
            }
        }
        children = new LinkedList<>();
    }
    
    //expand and form child nodes
    public void expand() {
        
        //loop over all possible moves and create nodes for each
        for (Cor piece : pieces) {
            LinkedList<Move> moves = board.calcMoves(piece);
            for (Move move : moves) {
                /* FUTURE: Calc all chain moves for each move 
                 * FUTURE: store values in a LinkedHashSet to avoid dupes
                 */
                
                Board b = new Board(board);
                b.doMove(move);
                GameNode child = new GameNode(b, !isMax);
                children.add(child);
            }
        }
    }
    
    
    public void print() {
        System.out.println("Root");
        board.print();
        System.out.println("Children");
        for (GameNode node : children) {
            node.board.print();
            System.out.println();
        }
    }
    
            
    public static void main(String[] args) {
        GameNode node = new GameNode(new Board(), true);
        node.expand();
        node.print();
    }
    
    private final boolean isMax;
    private Board board;
    private LinkedList<Cor> pieces;
    private LinkedList<GameNode> children;
    
}
