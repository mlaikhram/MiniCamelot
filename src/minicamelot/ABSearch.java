/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

/**
 *
 * @author Matthew Laikhram
 */
public class ABSearch implements Callable<String> {
//public class ABSearch implements Runnable {

    public ABSearch(PlayerAI _ai, Board b) {
        ai = _ai;
        board = b;
        bestMove = new Move();
        depth = 0;
        totalNodes = 0;
        maxPrunes = 0;
        minPrunes = 0;
    }
    
    
    @Override
    public String call() throws Exception {
        int currentDepth = 1;
        while (!Thread.interrupted()) {
            bestMove = ABSearchAlgo(currentDepth);
            ++currentDepth;
        }
        return "Ready!";
    }
    
    //class used in ABSearch to keep track of the best move
    private class Val {
        public Val(int _v, GameNode n) {
            v = _v;
            node = new GameNode(n);
        }
        
        public Val(Val val) {
            v = val.v;
            node = new GameNode(val.node);
        }
        
        public int v;
        public GameNode node;
    }
    
    
    //added depth limit to do an iterative deepening approach
    public Move ABSearchAlgo(int depthLimit) {
        GameNode node = new GameNode(board, true);
        Move ans;
        
        Val v = new Val(maxv(node, -1000, 1000, 0, depthLimit));
        //v.node.print();
        //System.out.println(v);
        node.expand();
        LinkedHashMap<GameNode, Move> moves = node.getChildren();
        ans = moves.get(v.node);
        //System.out.println("attempting to loop...");
        /*for (GameNode child : moves.keySet()) {
            //System.out.println("Checking child");
            if (ai.eval(child) == v) {
                //System.out.println("match found!");
                ans = new Move(moves.get(child));
                break;
            }
        }*/

        depth = v.v;
        //ans.print();
        return ans;
    }
    
    //added d to keep track of current depth
    private Val maxv(GameNode node, int a, int b, int d, int depthLimit) {
        if (ai.isTerminal(node) || d == depthLimit){
            return new Val(ai.eval(node), node);
        }
        Val v = new Val(-1000, node);
        
        //if node is not expanded, expand
        if (node.getChildren().isEmpty()) {
            node.expand();
        }
        LinkedHashMap<GameNode, Move> children = node.getChildren();
        for (GameNode child : children.keySet()){
            v = new Val(max(v.v, minv(child, a, b, d + 1, depthLimit).v), child);
            if (v.v >= b) {
                return v;
            }
            a = max(a, v.v);
        }
        return v;
    }
    
    //added d to keep track of current depth
    private Val minv(GameNode node, int a, int b, int d, int depthLimit) {
        if (ai.isTerminal(node) || d == depthLimit){
            return new Val(ai.eval(node), node);
        }
        Val v = new Val(1000, node);
        
        //if node is not expanded, expand
        if (node.getChildren().isEmpty()) {
            node.expand();
        }
        LinkedHashMap<GameNode, Move> children = node.getChildren();
        for (GameNode child : children.keySet()){
            //v.v = min(v.v, maxv(child, a, b, d + 1, depthLimit).v);
            v = new Val(min(v.v, maxv(child, a, b, d + 1, depthLimit).v), child);
            if (v.v >= b) {
                return v;
            }
            a = min(a, v.v);
        }
        return v;
    }
    
    
    private int min(int a, int b) {
        return a < b ? a : b;
    }
    
    
    private int max(int a, int b) {
        return a > b ? a : b;
    }
    
    
    private Val min(Val a, Val b) {
        return a.v < b.v ? a : b;
    }
    
    
    private Val max(Val a, Val b) {
        return a.v > b.v ? a : b;
    }
    
    
    public Move getMove() {
        return bestMove;
    }
    
    
    public int getDepth() {
        return depth;
    }
    
    
    private PlayerAI ai;
    private Board board;
    private Move bestMove;
    private int depth;
    private int totalNodes;
    private int maxPrunes;
    private int minPrunes;
    
}
