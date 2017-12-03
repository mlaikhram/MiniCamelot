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
        
        //title text
        c.gridx = 0;
        c.gridy = 0;
        c.fill = BOTH;
        c.weighty = 0.1;
        titleLabel = new JLabel("Mini Camelot");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 40));
        //titleLabel.setBackground(Color.gray);
        titleLabel.setOpaque(true);
        add(titleLabel, c);
        
        //difficulty select
        //c.gridx = 0;
        difficultyGroup = new ButtonGroup();
        ArrayList<String> diff = new ArrayList(Arrays.asList("Easy", "Medium", "Expert"));
        for (int i = 0; i < 3; ++i) {
            c.gridx = i;
            c.gridy = 1;
            c.weightx = 0.3;
            c.weighty = 0.3;
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
        
        //first player select
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.3;
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
        c.gridy = 2;
        c.weighty = 0.3;
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
    private int firstPlayer;
    private int difficulty;
    private ButtonGroup difficultyGroup;
    private ButtonGroup firstGroup; //determines who goes first
    
}
