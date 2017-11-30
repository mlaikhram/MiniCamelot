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
        depth = 0;
        nodes = 0;
        maxPrunes = 0;
        minPrunes = 0;
        time = 0;
    }
    
    
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
        
        System.out.println("max depth: " + depth); //not actually depth yet
        System.out.println("Total nodes generated: " + nodes);
        System.out.println("Total max prunes: " + maxPrunes);
        System.out.println("Total min prunes: " + minPrunes);
        algo.getMove().print();
        return algo.getMove();
    }
    
    //Random Search Algorithm
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
    
    
    public boolean isTerminal(GameNode node) {
        //node.expand();
        if (node.countPieces() == 0) {
            return true;
        }
        return node.getBoard().checkVictory() != -1;
    }
    
    //evaluation function
    public int eval(GameNode node) {
        Board b = node.getBoard();
        int black = 0;
        int white = 0;
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if (b.get(row, col) == Constants.BLACK) {
                    black += Constants.ROWS - row - 1;
                }
                else if (b.get(row, col) == Constants.WHITE) {
                    white += row;
                }
            }
        }
        return black - white;
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
    private int depth;
    private int nodes;
    private int maxPrunes;
    private int minPrunes;
    private double time;
    
}
