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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;


/**
 *
 * @author Matthew Laikhram
 * Defines the class that displays the board to the user graphically. It defines
 * the click event, which allows the user to interact with their pieces, as well
 * as highlights the possible moves for a selected piece. It also disables user
 * input while the AI is calculating a move.
 */
public class BoardGUI extends JPanel {
    
    //create a new BoardGUI using the given board
    public BoardGUI(MiniCamelot p, Board b, int diff, int firstPlayer) {
        
        //initialize to grid with size
        setLayout(new GridLayout(Constants.ROWS, Constants.COLS));
        setSize(400, 700);
        
        //initialize members
        parent = p;
        board = new Board(b);
        ai = new PlayerAI(diff);
        aiMove = null;
        if (firstPlayer == Constants.WHITE) {
            isPlayerTurn = true;
        }
        else {
            isPlayerTurn = false;
        }
        canChain = false;
        chain = new LinkedList<>();
        gameOver = false;
        //timer used to stagger player move and ai move visually
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
        //timer used to stagger chain moves for the ai visually
        chainTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chainTimer.stop();
                try {
                    if (!gameOver) {
                        aiChainMove();
                    }
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
        
        //let the ai start if it's their turn
        if (!isPlayerTurn) {
            if (parent != null) {
                parent.setText("AI is thinking...");
            }
            aiTimer.start();
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
                    if (parent != null) {
                        parent.setText("AI is thinking...");
                    }
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
        if (parent != null) {
            parent.setText(ai.getMood() + "\n" + ai.getStats());
        }
        chainTimer.start();
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
        if (parent != null) {
            if (gameState == 0) {
                parent.setText("It's a draw!\n" + parent.getText());
            }
            else if (gameState == Constants.BLACK) {
                parent.setText("You lose!\n" + parent.getText());
            }
            else if (gameState == Constants.WHITE) {
                parent.setText("You win!\n" + parent.getText());
            }
            else {
                return;
            }
        }
        gameOver = true;
    }
    
    //create an image icon give a piece value converted to a string
    public ImageIcon iconify(String piece) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = classLoader.getResource("img/" + piece + ".png").getPath();
        try {
            BufferedImage img = Constants.resize(ImageIO.read(new File(path)), 50, 50);
            return new ImageIcon(img);
        }
        catch (IOException e) {
            System.out.println("Could not find path " + path);
            return new ImageIcon();
        }
    }
    
    private MiniCamelot parent;
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
