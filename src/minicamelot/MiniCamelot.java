/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.awt.GridLayout;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author Matthew Laikhram
 */
public class MiniCamelot extends JFrame {

    public MiniCamelot() {
        
        setTitle("Mini Camelot");
        setLayout(new GridLayout(1, 2));
        setSize(800, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ImageIcon icon = new ImageIcon(Paths.get("", "src", "img", "MiniCamelotIcon.png").toAbsolutePath().toString());
        setIconImage(icon.getImage());
        
        board = null;
        //add(board);
        
        newGame = new NewGamePanel(this);
        add(newGame);
        
        player = new PlayerPanel(this);
        setText("AI will take a maximum of 10 seconds to calculate a move");
        add(player);
    }
    
    //create new game with the rules defined in the new game panel
    public void startGame() {
        board = new BoardGUI(this, new Board(), newGame.getDifficulty(), newGame.getFirstPlayer());
        getContentPane().removeAll();
        add(board);
        add(player);
        repaint();
        revalidate();
    }
    
    //ends the game and brings back the new game panel
    public void surrender() {
        if (board != null) {
            board = null;
            getContentPane().removeAll();
            add(newGame);
            add(player);
            repaint();
            revalidate();
        }
        else {
            System.exit(0);
        }
    }
    
    //set text on the player panel
    public void setText(String text) {
        player.setText(text);
    }
    
    //get text on the player panel
    public String getText() {
        return player.getText();
    }
    
    public static void main(String[] args) {
        MiniCamelot mc = new MiniCamelot();
        mc.setVisible(true);
    }
    
    private BoardGUI board;
    private NewGamePanel newGame;
    private PlayerPanel player;
    
}
