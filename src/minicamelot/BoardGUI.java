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
        isPlayerTurn = true;
        aiTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aiTimer.stop();
                aiMove();
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
        
        if (!isPlayerTurn) return;
        
        //if a valid piece is clicked on
        if (board.get(tile) == Constants.WHITE) {
            updateSelectedPiece(tile);
        }
        //if a valid move is selected
        else if (validMoves.containsKey(tile)) {
            board.doMove(validMoves.get(tile));
            moveSelectedPiece(tile);
            isPlayerTurn = false;
                        
            aiTimer.start();
        }
        else {
            updateSelectedPiece(null);
        }
        repaint();
        revalidate();     
    }
    
    //allows ai to make a move
    public void aiMove() {
        
        //use alpha-beta search to choose a move
        Move aiMove = ai.ABSearch(board);
            
        //calculate destination tile for selected piece
        selectedPiece = aiMove.piece;
        Cor dest = firstOpen(aiMove.piece, aiMove.dir);

        //update the board
        board.doMove(aiMove);
        moveSelectedPiece(dest);
        isPlayerTurn = true;
        
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
        
        //reset selected piece
        selectedPiece = null;
    }
    
    //returns the cors to the first open tile in the direction dir
    //returns start if no open tile is found
    private Cor firstOpen(Cor start, Cor dir) {
        Cor pos = new Cor(start);
        while (board.isValid(pos)) {
            pos = pos.add(dir);
            if (board.get(pos) == 0) {
                return pos;
            }
        }
        return start;
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
    private boolean isPlayerTurn;
    private Timer aiTimer;
    private ArrayList<ArrayList<JLabel>> tiles;
    private Cor selectedPiece;
    private HashMap<Cor, Move> validMoves;
    private HashMap<String, ImageIcon> imgs;
}
