/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        prunes = 0;
        time = 0;
    }
    
    
    public Move calcBestMove(Board b) throws InterruptedException {
        ABSearch algo = new ABSearch(this, b);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<?> future = executor.submit(algo);
        
        try {
            //System.out.println("Started..");
            try {
                System.out.println(future.get(2, TimeUnit.SECONDS));
            } catch (InterruptedException ex) {
                System.out.println("Interrupted Exception");
                Logger.getLogger(PlayerAI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                System.out.println("Execution Exception");
                Logger.getLogger(PlayerAI.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("Finished!");
        } catch (TimeoutException e) {
            System.out.println("Timeout");
            future.cancel(true);
            //System.out.println("Terminated!");
        }

        executor.shutdownNow();
        
        /*if (!executor.awaitTermination(100, TimeUnit.MICROSECONDS)) {
            System.out.println("Still waiting...");
            System.exit(0);
        }
        System.out.println("Exiting normally...");*/
        System.out.println("Shut down");
        
        System.out.println(algo.getDepth());
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
                    ++black;
                }
                else if (b.get(row, col) == Constants.WHITE) {
                    ++white;
                }
            }
        }
        return black - white;
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
    private int prunes;
    private double time;
    
}
