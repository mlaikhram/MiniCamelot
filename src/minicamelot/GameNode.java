/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

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
        children = new LinkedHashMap<>();
    }
    
    public GameNode(GameNode n) {
        board = new Board(n.board);
        isMax = n.isMax;
        pieces = new LinkedList<>(n.pieces);
        children = new LinkedHashMap<>();
    }
    
    //expand and form child nodes
    public void expand() {
        
        //loop over all possible moves and create nodes for each
        for (Cor piece : pieces) {
            LinkedList<Move> moves = board.calcMoves(piece);
            for (Move move : moves) {
                /* FUTURE: Calc all chain moves for each move 
                 */                
                Board b = new Board(board);
                b.doMove(move);
                GameNode child = new GameNode(b, !isMax);
                children.put(child, move);
            }
        }
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameNode other = (GameNode) obj;
        if (this.isMax != other.isMax) {
            return false;
        }
        if (!this.board.equals(other.board)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.isMax ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.board);
        return hash;
    }
    
    
    public LinkedHashMap<GameNode, Move> getChildren() {
        return children;
    }
    
    
    public boolean isMax() {
        return isMax;
    }
    
    public Board getBoard() {
        return board;
    }
    
    
    public void print() {
        System.out.println("Root");
        board.print();
        System.out.println("Children");
        for (GameNode node : children.keySet()) {
            node.board.print();
            System.out.println();
        }
        System.out.println(children.size() + " children");
    }
    
            
    public static void main(String[] args) {
        GameNode node = new GameNode(new Board(), true);
        node.expand();
        node.print();
    }
    
    private final boolean isMax;
    private Board board;
    private LinkedList<Cor> pieces;
    private LinkedHashMap<GameNode, Move> children; //linkedhashmap keeps the initial order of entries
    
}
