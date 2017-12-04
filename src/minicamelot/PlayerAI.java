/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Matthew Laikhram
 */
public class PlayerAI {
    
    public PlayerAI(int diff) {
        difficulty = diff;
        val = 0;
        depth = 0;
        nodes = 0;
        maxPrunes = 0;
        minPrunes = 0;
    }
    
    //run the ABSearch in a new thread with a time limit of 10 seconds
    public Move calcBestMove(Board b) throws Exception {
        ABSearch algo = new ABSearch(this, b);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(algo);

        try {
            System.out.println(future.get(10, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            future.cancel(true);
        }

        executor.shutdownNow();

        return algo.getMove();
    }
    
    //flavor text based on how good the searched move is
    public String getMood() {
        if (val < 0) {
            return "This does not look good...";
        }
        else if (val > 0) {
            return "I can do this with my eyes closed!";
        }
        return "hmm...";
    }
    
    //return the relevant stats from the alpha beta search
    public String getStats() {
        return  "Max depth: " + depth + "\n" + 
                "Total nodes generated: " + nodes + "\n" + 
                "Total max prunes: " + maxPrunes + "\n" + 
                "Total min prunes: " + minPrunes;
    }
 
    
    public boolean isTerminal(GameNode node) {
        GameNode temp = new GameNode(node);
        temp.expand();
        if (temp.getChildren().isEmpty()) {
            return true;
        }
        return node.getBoard().checkVictory() != -1;
    }
    
    //evaluation function
    public int eval(GameNode node) {
        Board b = node.getBoard();
        
        //check victory states
        int gameOver = b.checkVictory();
        switch (gameOver) {
            case 0:
                return 0;
            case Constants.BLACK:
                return 1000;
            case Constants.WHITE:
                return -1000;
        }
        
        //count pieces for each player and make the value of each piece:
        //20 + distance to opposing castle
        int black = 0;
        int white = 0;
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if (b.get(row, col) == Constants.BLACK) {
                    int dist = Constants.ROWS - row - 1;
                    black += 20 + dist;
                }
                else if (b.get(row, col) == Constants.WHITE) {
                    white += 20 + row;
                }
            }
        }
        return black - white;
    }
    
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public void setVal(int newVal) {
        val = newVal;
    }
    
    public void setDepth(int newDepth) {
        depth = newDepth;
    }
    
    public void setNodes(int newNodes) {
        nodes = newNodes;
    }
    
    public void setMaxPrunes(int prunes) {
        maxPrunes = prunes;
    }
    
    public void setMinPrunes(int prunes) {
        minPrunes = prunes;
    }

    
    private int difficulty;
    private int val;
    private int depth;
    private int nodes;
    private int maxPrunes;
    private int minPrunes;  
}
