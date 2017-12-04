/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

/**
 *
 * @author Matthew Laikhram
 */
public class NewGamePanel extends JPanel {
    
    public NewGamePanel(MiniCamelot p) {
        
        //initialize to grid with size
        setLayout(new GridBagLayout());
        setSize(400, 700);
        
        //allow access to the outermost frame
        parent = p;
        
        //use gridbag to format components
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.ipady = 0;
        
        //title text
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.fill = BOTH;
        c.weighty = 0.1;
        titleLabel = new JLabel("Mini Camelot");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 40));
        titleLabel.setBackground(Color.gray);
        titleLabel.setOpaque(true);
        add(titleLabel, c);
        
        //rules label
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.weighty = 0.4;
        rules = new JLabel("<html>Rules:<br>You control the white pieces. "
                + "The goal of the game is to either get 2 of your pieces into the opposing castle (bottom 2 tiles) or "
                + "to capture all enemy pieces while still having 2 pieces remaining. If both players have less than 2 pieces remaining, then it is a draw."
                + " You can perform 3 types of moves:<br><br>"
                + "Plain Move: Move a piece to any empty adjacent tile<br><br>"
                + "Canter Move: Move a piece over one of your adjacent pieces to an empty tile immediately beyond<br><br>"
                + "Capture Move: Move a piece over one of the opposing adjacent pieces to an empty tile immediately beyond, and remove the opposing piece<br><br>"
                + "Capture Moves are mandatory whenever they are immediately available.<br><br>"
                + "You can chain together multiple Canter and Capture Moves. Chaining Canter Moves together will prevent you from landing on any tile you've landed on in the current chain."
                + " However, if a Capture Move is made in the chain, then any tiles landed on prior to the Capture Move will be allowed in the chain once again."
                + " While Capture Moves that are beyond a chain are not required, if you chain moves up to the point of having a Capture Move immediately available, then you must take the Capture Move.");
        add(rules, c);
        
        //difficulty label
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.weighty = 0.1;
        difficultyLabel = new JLabel("Difficulty");
        add(difficultyLabel, c);
        
        //difficulty select
        //c.gridx = 0;
        difficultyGroup = new ButtonGroup();
        ArrayList<String> diff = new ArrayList(Arrays.asList("Easy", "Medium", "Expert"));
        for (int i = 0; i < 3; ++i) {
            c.gridx = i;
            c.gridy = 3;
            c.gridwidth = 1;
            c.weightx = 0.1;
            c.weighty = 0.1;
            final int d = i;
            JRadioButton r = new JRadioButton(diff.get(i));
            //r.setActionCommand("" + i);
            if (i == 0) {
                r.setSelected(true);
            }
            r.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    difficulty = d;
                }
            });
            difficultyGroup.add(r);
            add(r, c);
        }
        
        //first player label
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        c.weighty = 0.1;
        firstPlayerLabel = new JLabel("First Player");
        add(firstPlayerLabel, c);
        
        //first player select
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        c.weighty = 0.1;
        firstGroup = new ButtonGroup();
        JRadioButton w = new JRadioButton("You");
        //r.setActionCommand("" + i);
        w.setSelected(true);
        w.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstPlayer = Constants.WHITE;
            }
        });
        firstGroup.add(w);
        add(w, c);
        c.gridx = 1;
        c.gridy = 5;
        c.weighty = 0.1;
        JRadioButton b = new JRadioButton("AI");
        //r.setActionCommand("" + i);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstPlayer = Constants.BLACK;
            }
        });
        firstGroup.add(b);
        add(b, c);
        
        //start button
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 3;
        c.weighty = 0.1;
        start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parent != null) {
                    parent.startGame();
                }
            }
        });
        add(start, c);
    }
    
    //accessors
    public int getFirstPlayer() {
        return firstPlayer;
    }
    
    public int getDifficulty() {
        return difficulty;
    }

    
    public static void main(String[] args) {
        JFrame jf = new JFrame("Mini Camelot");
        jf.setSize(400, 700);
        jf.setResizable(false);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        NewGamePanel p = new NewGamePanel(null);
        //p.setText("some text\nmore text\neven more text\nHOW MUCH TEXT");
        jf.add(p);
        jf.setVisible(true);
    }
    
    
    private MiniCamelot parent;
    private JLabel titleLabel;
    private JLabel rules;
    private JLabel difficultyLabel;
    private JLabel firstPlayerLabel;
    private int difficulty;
    private int firstPlayer;
    private ButtonGroup difficultyGroup;
    private ButtonGroup firstGroup; //determines who goes first
    private JButton start;
    
}
