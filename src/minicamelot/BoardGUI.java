/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.SwingConstants;


/**
 *
 * @author Matthew Laikhram
 */
public class BoardGUI extends JPanel {
    
    //create a new BoardGUI using the given board
    public BoardGUI(Board b) {
        
        setLayout(new GridLayout(Constants.ROWS, Constants.COLS));
        setSize(400, 700);
        
        board = new Board(b);
        tiles = new ArrayList<>(Constants.ROWS);
        selectedPiece = null;
        validMoves = new LinkedList<>();
        
        for (int row = 0; row < Constants.ROWS; ++row) {
            tiles.add(new ArrayList<JLabel>(Constants.COLS));
        }
        
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GRAY);
        colors.add(Color.BLACK);
        colors.add(Color.WHITE);

        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                final int r = row;
                final int c = col;
                JLabel tile = new JLabel();
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        tileClicked(r, c);
                    } 
                });
                tile.setBackground(colors.get(board.get(row, col) + 1));
                tile.setOpaque(true);
                tile.setText("" + board.get(row, col));
                tile.setHorizontalAlignment(SwingConstants.CENTER);
                tile.setVerticalAlignment(SwingConstants.CENTER);
                tiles.get(row).add(col, tile);
                //tiles.get(row).get(col).setText("" + board.get(row, col));
                add(tiles.get(row).get(col));
            }
        }
    }
    
    public void tileClicked(int row, int col) {
        //if a valid piece is clicked on
        if (board.get(row, col) == Constants.WHITE) {
            selectedPiece = new Cor(col, row);
            
        }
    }
    
    public void updateValidMoves() {
        for (Move m : validMoves) {
            
        }
    }
    
    public void updateTiles() {
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                
            }
        }
    }
    
    public static void main(String[] args) {
        JFrame jf = new JFrame("Mini Camelot");
        jf.setSize(400, 700);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        BoardGUI board = new BoardGUI(new Board());
        jf.add(board);
        jf.setVisible(true);
    }
    
    private Board board;
    private ArrayList<ArrayList<JLabel>> tiles;
    private Cor selectedPiece;
    private LinkedList<Move> validMoves;
}
