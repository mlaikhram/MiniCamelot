/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.Timer;


/**
 *
 * @author Matthew Laikhram
 */
public class BoardGUI extends JPanel {
    
    //create a new BoardGUI using the given board
    public BoardGUI(Board b) {
        
        //initialize to grid with size
        setLayout(new GridLayout(Constants.ROWS, Constants.COLS));
        setSize(400, 700);
        
        //initialize members
        board = new Board(b);
        ai = new PlayerAI(3);
        aiMove = null;
        isPlayerTurn = true;
        canChain = false;
        chain = new LinkedList<>();
        gameOver = false;
        aiTimer = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aiTimer.stop();
                try {
                    aiMove();
                } catch (Exception ex) {
                    System.out.println("AI exploded");
                    Logger.getLogger(BoardGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        chainTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chainTimer.stop();
                try {
                    aiChainMove();
                } catch (Exception ex) {
                    System.out.println("AI exploded");
                    Logger.getLogger(BoardGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        tiles = new ArrayList<>(Constants.ROWS);
        for (int row = 0; row < Constants.ROWS; ++row) {
            tiles.add(new ArrayList<JLabel>(Constants.COLS));
        }
        selectedPiece = null;
        validMoves = new HashMap<>();
        imgs = new HashMap<>();
        
        //populate imgs
        imgs.put("-2", iconify("-2"));
        imgs.put("-1", iconify("-1"));
        imgs.put("0", iconify("0"));
        imgs.put("0valid", iconify("0valid"));
        imgs.put("1", iconify("1"));
        imgs.put("2", iconify("2"));
        imgs.put("2valid", iconify("2valid"));
        imgs.put("2selected", iconify("2selected"));


        //for each position in the matrix of tiles, create a tile (jlabel)
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                final int r = row;
                final int c = col;
                JLabel tile = new JLabel();
                
                //add a click event for each tile
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        tileClicked(new Cor(c, r));
                    } 
                });
                
                //set the appropriate sprite for each tile
                tile.setIcon(imgs.get("" + board.get(row, col)));
                tiles.get(row).add(col, tile);
                add(tiles.get(row).get(col));
            }
        }
    }
    
    //determine what to do depending on what tile the user clicks on
    public void tileClicked(Cor tile) {
        
        if (!isPlayerTurn || gameOver) return;
        
        //if a valid piece is clicked on while you are not trying to chain
        if (!canChain && board.get(tile) == Constants.WHITE) {
            updateSelectedPiece(tile);
        }
        //if a valid move is selected
        else if (validMoves.containsKey(tile)) {
            
            Move m = new Move(validMoves.get(tile));
            LinkedList<Move> captureMoves = board.calcCaptureMoves(selectedPiece);
            LinkedList<Move> chainable = board.calcCanterMoves(selectedPiece);
            chainable.addAll(captureMoves);
            
            //do the move and update the board
            board.doMove(m);
            moveSelectedPiece(tile);
            
            //if player can chain from this move, check for chain moves
            if (!m.dir.equals(Constants.X) && chainable.contains(m)) {
                //if the move you just made was a capture move, reset the chain
                if (captureMoves.contains(m)){
                    chain.clear();
                }
                updateChainMoves(tile);
            }
            else {
                canChain = false;
            }
            
            //if player can't make a chain move, let the ai go
            if (!canChain) {
                isPlayerTurn = false;
                chain.clear();
                
                if (!gameOver) {
                    aiTimer.start();
                }
            }
        }
        //if the player clicked elsewhere and is not in a chain, then unclick the currentPiece
        else if (!canChain) {
            updateSelectedPiece(null);
        }
        repaint();
        revalidate();     
    }
    
    //allows ai to make a move
    public void aiMove() throws Exception {
        
        //use alpha-beta search to choose a move
        aiMove = ai.calcBestMove(board);
        
        chainTimer.start();
        //calculate destination tile for selected piece
        selectedPiece = aiMove.piece;
        Cor dest = firstOpen(aiMove.piece, aiMove.dir);

        //update the board
        board.doMove(aiMove);
        moveSelectedPiece(dest);
        isPlayerTurn = true;
        
        System.out.println("Current board value: " + ai.eval(new GameNode(board, false)));
        
        repaint();
        revalidate();
    }
    
    //allows ai to chain moves together and show it visually
    public void aiChainMove() throws Exception {
        //calculate destination tile for selected piece
        selectedPiece = aiMove.piece;
        Cor dest = firstOpen(aiMove.piece, aiMove.dir);

        //update the board
        board.doMove(aiMove);
        moveSelectedPiece(dest);
        
        //if there is a chain move, then repeat
        aiMove = aiMove.chain;
        if (aiMove != null) {
            chainTimer.start();
        }
        //otherwise end the ai turn
        else {
            System.out.println("Current board value: " + ai.eval(new GameNode(board, false)));
            isPlayerTurn = true;
        }
        repaint();
        revalidate();
    }
    
    //update to the most recently clicked on piece, along with valid moves
    public void updateSelectedPiece(Cor piece) {
        //if there is an old selected piece, reset its sprite and valid moves      
        if (selectedPiece != null) {
            tiles.get(selectedPiece.y).get(selectedPiece.x).setIcon(imgs.get("" + board.get(selectedPiece)));
            
            for (Cor dest : validMoves.keySet()) {
                tiles.get(dest.y).get(dest.x).setIcon(imgs.get("" + board.get(dest)));
            }
            validMoves.clear();
        }
        
        //if no piece was selected or the same piece was selected, reset the selectedPiece
        if (piece == null || (selectedPiece != null && selectedPiece.equals(piece))) {
            selectedPiece = null;
            return;
        }
        
        //set new piece to selected and update sprite
        selectedPiece = new Cor(piece);
        tiles.get(piece.y).get(piece.x).setIcon(imgs.get("" + board.get(piece) + "selected"));
        
        //update valid moves
        LinkedList<Move> moves = board.calcMoves(piece);
        for (Move m : moves) {
            Cor dest = firstOpen(piece, m.dir);
            validMoves.put(dest, m);
            tiles.get(dest.y).get(dest.x).setIcon(imgs.get("" + board.get(dest) + "valid"));
        }
    }
    
    //moves the selected piece to the chosen destination tile and updates the board
    public void moveSelectedPiece(Cor destPos) {
        //set old position tile to open sprite
        tiles.get(selectedPiece.y).get(selectedPiece.x).setIcon(imgs.get("" + 0));
        
        //reset valid moves
        for (Cor dest : validMoves.keySet()) {
            tiles.get(dest.y).get(dest.x).setIcon(imgs.get("" + board.get(dest)));
        }
        validMoves.clear();
        
        //update destination pos sprite
        tiles.get(destPos.y).get(destPos.x).setIcon(imgs.get("" + board.get(destPos)));
        
        //update hopped tile
        Cor mid = destPos.mid(selectedPiece);
        tiles.get(mid.y).get(mid.x).setIcon(imgs.get("" + board.get(mid)));
        
        //add selected piece's cors to the chain list
        chain.add(selectedPiece);
        
        //reset selected piece
        selectedPiece = null;
        
        //check for game over state
        checkForGameOver();
    }
    
    //updates board to display chain moves, if any
    public void updateChainMoves(Cor piece) {
        LinkedList<Move> chainMoves = board.calcSingleChainMoves(piece, chain);
        
        //if there are no chain moves, exit
        if (chainMoves.isEmpty()) {
            chain.clear();
            canChain = false;
            return;
        }
        canChain = true;
        
        //set new piece to selected and update sprite
        selectedPiece = new Cor(piece);
        tiles.get(piece.y).get(piece.x).setIcon(imgs.get("" + board.get(piece) + "selected"));
        
        //update valid moves
        for (Move m : chainMoves) {
            Cor dest = firstOpen(piece, m.dir);
            validMoves.put(dest, m);
            tiles.get(dest.y).get(dest.x).setIcon(imgs.get("" + board.get(dest) + "valid"));
        }
    }
    
    //returns the cors to the first open tile in the direction dir
    //returns start if no open tile is found
    private Cor firstOpen(Cor start, Cor dir) {
        //if the dir is 0,0 then return current pos
        if (dir.equals(Constants.X)) {
            return start;
        }
        Cor pos = new Cor(start);
        while (board.isValid(pos)) {
            pos = pos.add(dir);
            if (board.get(pos) == 0) {
                return pos;
            }
        }
        return start;
    }
    
    
    public void checkForGameOver() {
        int gameState = board.checkVictory();
        if (gameState == -1) {
            return;
        }
        if (gameState == 0) {
            System.out.println("It's a draw!");
        }
        else if (gameState == Constants.BLACK) {
            System.out.println("You lose!");
        }
        else if (gameState == Constants.WHITE) {
            System.out.println("You win!");
        }
        else {
            return;
        }
        gameOver = true;
    }
    
    
    public void updateTiles() {
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                tiles.get(row).get(col).setIcon(imgs.get("" + board.get(row, col)));
            }
        }
    }
    
    //create an image icon give a piece value converted to a string
    public ImageIcon iconify(String piece) {
        try {
            String path = Paths.get("", "src", "img", piece + ".png").toAbsolutePath().toString();
            BufferedImage img = Constants.resize(ImageIO.read(new File(path)), 50, 50);
            return new ImageIcon(img);
        }
        catch (IOException e) {
            String path = Paths.get("", "src", "img").toAbsolutePath().toString();
            System.out.println("Error: Image not found");
            System.out.println("Current path to img is: " + path);
            return new ImageIcon();
        }
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
    
    private Board board;
    private PlayerAI ai;
    private Move aiMove;
    private boolean isPlayerTurn;
    private boolean canChain;
    private LinkedList<Cor> chain; //keeps track of tiles in the current canter chain
    private boolean gameOver;
    private Timer aiTimer;
    private Timer chainTimer;
    private ArrayList<ArrayList<JLabel>> tiles;
    private Cor selectedPiece;
    private HashMap<Cor, Move> validMoves;
    private HashMap<String, ImageIcon> imgs;
}
