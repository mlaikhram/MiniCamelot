/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.awt.GridLayout;
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
        
        board = new BoardGUI(this, new Board());
        add(board);
        
        player = new PlayerPanel(this);
        add(player);
    }
    
    //create new game with the rules defined in the new game panel
    public void startGame() {
        
    }
    
    //ends the game and brings back the new game panel
    public void surrender() {
        System.out.println("I surrender!");
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
    private PlayerPanel player;
    
}
