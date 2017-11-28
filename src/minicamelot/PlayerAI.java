/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Matthew Laikhram
 */
public class PlayerAI {
    
    public PlayerAI(int diff) {
        difficulty = diff;
        depth = 0;
        nodes = 0;
        prunes = 0;
        time = 0;
    }
    
    //Alpha-Beta Search Algorithm
    public Move RandomSearch(Board b) {
        LinkedList<Move> moves = new LinkedList<>();
        
        //get all possible moves
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if (b.get(row, col) == Constants.BLACK) {
                    moves.addAll(b.calcMoves(new Cor(col, row)));
                }
            }
        }
        
        //pick a random move to do
        int index = new Random().nextInt(moves.size());
        return moves.get(index);
    }
    
    
    public Move ABSearch(Board b) {
        GameNode node = new GameNode(b, true);
        Move ans = new Move();
        
        int v = maxv(node, -1000, 1000);
        
        LinkedHashMap<GameNode, Move> moves = node.getChildren();
        for (GameNode child : moves.keySet()) {
            System.out.println("Checking child");
            if (eval(child) == v) {
                System.out.println("match found!");
                ans = new Move(moves.get(child));
                break;
            }
        }
        
        ans.print();
        return ans;
        //return RandomSearch(b);
    }
    
    public int maxv(GameNode node, int a, int b) {
        if (isTerminal(node)){
            return eval(node);
        }
        int v = -1000;
        
        //if node is not expanded, expand
        if (node.getChildren().isEmpty()) {
            node.expand();
        }
        LinkedHashMap<GameNode, Move> children = node.getChildren();
        for (GameNode child : children.keySet()){
            v = max(v, minv(child, a, b));
            if (v >= b) {
                return v;
            }
            a = max(a, v);
        }
        return v;
    }
    
    public int minv(GameNode node, int a, int b) {
        if (isTerminal(node)){
            return eval(node);
        }
        int v = 1000;
        
        //if node is not expanded, expand
        if (node.getChildren().isEmpty()) {
            node.expand();
        }
        LinkedHashMap<GameNode, Move> children = node.getChildren();
        for (GameNode child : children.keySet()){
            v = min(v, maxv(child, a, b));
            if (v >= b) {
                return v;
            }
            a = min(a, v);
        }
        return v;
    }
    
    public int min(int a, int b) {
        return a < b ? a : b;
    }
    
    public int max(int a, int b) {
        return a > b ? a : b;
    }
    
    public boolean isTerminal(GameNode node) {
        return !node.isMax();
        //return true;
    }
    
    //evaluation function
    public int eval(GameNode node) {
        return 0;
    }
    
    
    public static void main(String[] args) {
        Board b = new Board();
        b.print();
        PlayerAI ai = new PlayerAI(3);
        ai.ABSearch(b).print();
    }
    
    private int difficulty;
    private int depth;
    private int nodes;
    private int prunes;
    private double time;
    
}
