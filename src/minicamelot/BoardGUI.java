/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.awt.GridLayout;
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


/**
 *
 * @author Matthew Laikhram
 */
public class BoardGUI extends JPanel {
    
    //create a new BoardGUI using the given board
    public BoardGUI(Board b) throws IOException {
        
        //initialize to grid with size
        setLayout(new GridLayout(Constants.ROWS, Constants.COLS));
        setSize(400, 700);
        
        //initialize members
        board = new Board(b);
        tiles = new ArrayList<>(Constants.ROWS);
        selectedPiece = null;
        validMoves = new HashMap<>();
        
        for (int row = 0; row < Constants.ROWS; ++row) {
            tiles.add(new ArrayList<JLabel>(Constants.COLS));
        }

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
                tile.setIcon(iconify("" + board.get(row, col)));
                tiles.get(row).add(col, tile);
                add(tiles.get(row).get(col));
            }
        }
    }
    
    //determine what to do depending on what tile the user clicks on
    public void tileClicked(Cor tile) {

        //if a valid piece is clicked on
        if (board.get(tile) == Constants.WHITE) {
            updateSelectedPiece(tile);
        }
        //if a valid move is selected
        else if (validMoves.containsKey(tile)) {
            board.doMove(validMoves.get(tile));
            moveSelectedPiece(tile);
        }
        else {
            updateSelectedPiece(null);
        }
        board.mustCapture = board.mustCapture(Constants.WHITE);
        repaint();
        revalidate();
    }
    
    //update to the most recently clicked on piece, along with valid moves
    public void updateSelectedPiece(Cor piece) {
        //if there is an old selected piece, reset its sprite and valid moves      
        if (selectedPiece != null) {
            tiles.get(selectedPiece.y).get(selectedPiece.x).setIcon(iconify("" + board.get(selectedPiece)));
            
            for (Cor dest : validMoves.keySet()) {
                tiles.get(dest.y).get(dest.x).setIcon(iconify("" + board.get(dest)));
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
        tiles.get(piece.y).get(piece.x).setIcon(iconify("" + board.get(piece) + "selected"));
        
        //update valid moves
        LinkedList<Move> moves = board.calcMoves(piece);
        for (Move m : moves) {
            Cor dest = firstOpen(piece, m.dir);
            validMoves.put(dest, m);
            tiles.get(dest.y).get(dest.x).setIcon(iconify("" + board.get(dest) + "selected"));
        }
    }
    
    //moves the selected piece to the chosen destination tile and updates the board
    public void moveSelectedPiece(Cor destPos) {
        //set old position tile to open sprite
        tiles.get(selectedPiece.y).get(selectedPiece.x).setIcon(iconify("" + 0));
        
        //reset valid moves
        for (Cor dest : validMoves.keySet()) {
            tiles.get(dest.y).get(dest.x).setIcon(iconify("" + board.get(dest)));
        }
        validMoves.clear();
        
        //update destination pos sprite
        tiles.get(destPos.y).get(destPos.x).setIcon(iconify("" + board.get(destPos)));
        
        //update hopped tile
        Cor mid = destPos.mid(selectedPiece);
        tiles.get(mid.y).get(mid.x).setIcon(iconify("" + board.get(mid)));
        
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
    
    /*
    public void updateTiles() {
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                
            }
        }
    }*/
    
    //create an image icon give a piece value converted to a string
    public ImageIcon iconify(String piece) {
        try {
            String path = Paths.get("", "src", "img", piece + ".jpg").toAbsolutePath().toString();
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
        
        try {
            BoardGUI board = new BoardGUI(new Board());
            jf.add(board);
        }
        catch (IOException e) {
            String path = Paths.get("", "src", "img").toAbsolutePath().toString();
            System.out.println("Error: Image not found");
            System.out.println("Current path to img is: " + path);
        }
        jf.setVisible(true);
    }
    
    private Board board;
    private ArrayList<ArrayList<JLabel>> tiles;
    private Cor selectedPiece;
    private HashMap<Cor, Move> validMoves;
}
