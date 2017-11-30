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
        totalNodes = 1;
        maxPrunes = 0;
        minPrunes = 0;
    }
    
    
    @Override
    public String call() {
        int currentDepth = 1;
        while (0 == 0) {
            try { 
                bestMove = ABSearchAlgo(currentDepth);
                ++currentDepth;
                ai.setDepth(depth);
                ai.setNodes(totalNodes);
                ai.setMaxPrunes(maxPrunes);
                ai.setMinPrunes(minPrunes);
            } catch (InterruptedException ex) {
                break;
            }
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
    public Move ABSearchAlgo(int depthLimit) throws InterruptedException {
        GameNode node = new GameNode(board, true);
        Move ans;
        
        Val v = new Val(maxv(node, -1000, 1000, 0, depthLimit));
        //v.node.print();
        //System.out.println(v);
        //node.expand();
        LinkedHashMap<GameNode, Move> moves = node.getChildren();
        //v.node.print();
        ans = moves.get(v.node);

        //depth = v.v;
        //ans.print();
        return ans;
    }
    
    //added d to keep track of current depth
    private Val maxv(GameNode node, int a, int b, int d, int depthLimit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        depth = d;
        if (ai.isTerminal(node) || d == depthLimit){
            return new Val(ai.eval(node), node);
        }
        Val v = new Val(-1000, node);
        
        //if node is not expanded, expand
        if (node.getChildren().isEmpty()) {
            node.expand();
            //node.print();
            totalNodes += node.getChildren().size();
        }
        LinkedHashMap<GameNode, Move> children = node.getChildren();
        for (GameNode child : children.keySet()){
            v = new Val(max(v.v, minv(child, a, b, d + 1, depthLimit).v), child);
            if (v.v >= b) {
                ++maxPrunes;
                return v;
            }
            a = max(a, v.v);
        }
        return v;
    }
    
    //added d to keep track of current depth
    private Val minv(GameNode node, int a, int b, int d, int depthLimit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        depth = d;
        if (ai.isTerminal(node) || d == depthLimit){
            return new Val(ai.eval(node), node);
        }
        Val v = new Val(1000, node);
        
        //if node is not expanded, expand
        if (node.getChildren().isEmpty()) {
            node.expand();
            //node.print();
            totalNodes += node.getChildren().size();
        }
        LinkedHashMap<GameNode, Move> children = node.getChildren();
        for (GameNode child : children.keySet()){
            //v.v = min(v.v, maxv(child, a, b, d + 1, depthLimit).v);
            v = new Val(min(v.v, maxv(child, a, b, d + 1, depthLimit).v), child);
            if (v.v <= a) {
                ++minPrunes;
                return v;
            }
            b = min(b, v.v);
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
