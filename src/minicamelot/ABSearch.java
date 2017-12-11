/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.concurrent.Callable;

/**
 *
 * @author Matthew Laikhram
 * Defines the class used to run the alpha-beta search algorithm
 * The PlayerAI creates an instance of this callable and runs the algorithm
 * in a separate thread until 10 seconds are up.
 * It then receives the stats and selected move from this object
 */
public class ABSearch implements Callable<String> {

    public ABSearch(PlayerAI _ai, Board b) {
        ai = _ai;
        board = b;
        bestMove = new Move();
        val = 0;
        depth = 0;
        totalNodes = 1;
        maxPrunes = 0;
        minPrunes = 0;
    }
    
    //do an iterative deepening approach to have a backup answer in the event that time runs out
    @Override
    public String call() {
        
        //limit depth based on difficulty of the ai
        final int diff = ai.getDifficulty();
        int maxDepth = -1;
        int depthLimit = 3;
        switch (diff) {
            case 0:
                maxDepth = 2;
                depthLimit = 1;
                break;
            case 1:
                maxDepth = 3;
                depthLimit = 2;
                break;
            default:
                break;
        }
        
        //run the algorithm as long as you have time, or until the maxDepth is reached
        while (maxDepth < 0 || depthLimit < maxDepth) {
            try { 
                bestMove = ABSearchAlgo(depthLimit);

                ai.setVal(val);
                ai.setDepth(depth);
                ai.setNodes(totalNodes);
                ai.setMaxPrunes(maxPrunes);
                ai.setMinPrunes(minPrunes);
                
                //if the max depth of the game tree has been passed by the depth limit, then finish
                if (depth < depthLimit) {
                    break;
                }
                
                ++depthLimit;
            } catch (InterruptedException ex) {
                break;
            }
        }
        return "";
    }
    
    //class used in ABSearch to keep track of the best move
    private class Val {
        public Val(int _v, GameNode n) {
            v = _v;
            if (n == null) {
                node = null;
            }
            else {
                node = new GameNode(n);
            }
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
        val = v.v;

        ans = node.getChildren().get(v.node);
        return ans;
    }
    
    //added d to keep track of current depth
    private Val maxv(GameNode node, int a, int b, int d, int depthLimit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        depth = Constants.max(depth, d);
        if (ai.isTerminal(node) || d == depthLimit){
            return new Val(ai.eval(node), node);
        }
        Val v = new Val(-1000, node);
        
        //if node is not expanded, expand
        if (node.getChildren().isEmpty()) {
            node.expand();
            totalNodes += node.getChildren().size();
        }
        for (GameNode child : node.getChildren().keySet()){
            Val t = new Val(minv(child, a, b, d + 1, depthLimit).v, child);
            v = new Val(max(v, t));
            if (v.v >= b) {
                ++maxPrunes;
                return v;
            }
            a = Constants.max(a, v.v);
        }
        return v;
    }
    
    //added d to keep track of current depth
    private Val minv(GameNode node, int a, int b, int d, int depthLimit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        depth = Constants.max(depth, d);
        if (ai.isTerminal(node) || d == depthLimit){
            return new Val(ai.eval(node), node);
        }
        Val v = new Val(1000, node);
        
        //if node is not expanded, expand
        if (node.getChildren().isEmpty()) {
            node.expand();
            totalNodes += node.getChildren().size();
        }
        for (GameNode child : node.getChildren().keySet()){
            Val t = new Val(maxv(child, a, b, d + 1, depthLimit).v, child);
            v = new Val(min(v, t));
            if (v.v <= a) {
                ++minPrunes;
                return v;
            }
            b = Constants.min(b, v.v);
        }
        return v;
    }
    

    private Val min(Val a, Val b) {
        return (a.v < b.v) ? new Val(a) : new Val(b);
    }
    
    
    private Val max(Val a, Val b) {
        return (a.v > b.v) ? new Val(a) : new Val(b);
    }
    
    //accessors
    public Move getMove() {
        return bestMove;
    }
    
    public int getDepth() {
        return depth;
    }
    
    
    private PlayerAI ai;
    private Board board;
    private Move bestMove;
    private int val;
    private int depth;
    private int totalNodes;
    private int maxPrunes;
    private int minPrunes; 
}
