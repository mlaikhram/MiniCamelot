/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JFrame;

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
        time = 0;
    }
    
    //run the ABSearch in a new thread with a time limit of 10 seconds
    public Move calcBestMove(Board b) throws Exception {
        ABSearch algo = new ABSearch(this, b);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(algo);

        try {
            System.out.println("Started..");
            System.out.println(future.get(10, TimeUnit.SECONDS));
            System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Terminated!");
        }

        executor.shutdownNow();
        
        System.out.println("Move value: " + val);
        System.out.println("Max depth: " + depth);
        System.out.println("Total nodes generated: " + nodes);
        System.out.println("Total max prunes: " + maxPrunes);
        System.out.println("Total min prunes: " + minPrunes);
        algo.getMove().print();
        return algo.getMove();
    }
    
    //Random Search Algorithm
    /*public Move RandomSearch(Board b) {
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
    }*/
    
    
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
        
        int black = 0;
        int white = 0;
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if (b.get(row, col) == Constants.BLACK) {
                    int dist = Constants.ROWS - row - 1;
                    black += dist;
                }
                else if (b.get(row, col) == Constants.WHITE) {
                    white += row;
                }
            }
        }
        //return 0;
        return black - white;
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
    
    
    public static void main(String[] args) {
        JFrame jf = new JFrame("Mini Camelot");
        jf.setSize(400, 700);
        jf.setResizable(false);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        BoardGUI board = new BoardGUI(new Board());
        jf.add(board);
        jf.setVisible(true);
    }
    
    private int difficulty;
    private int val;
    private int depth;
    private int nodes;
    private int maxPrunes;
    private int minPrunes;
    private double time;
    
}
