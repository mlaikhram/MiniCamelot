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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Matthew Laikhram
 */
public class PlayerPanel extends JPanel {
    
    public PlayerPanel(MiniCamelot p) {
        
        //initialize to grid with size
        setLayout(new GridBagLayout());
        setSize(400, 700);
        
        //allow access to the outermost frame
        parent = p;
        
        //use gridbag to format components
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        
        //text area
        c.gridx = 0;
        c.gridy = 0;
        c.fill = BOTH;
        c.weighty = 0.95;
        textLabel = new JLabel();
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        textLabel.setFont(new Font(textLabel.getFont().getName(), Font.PLAIN, 20));
        textLabel.setBackground(Color.gray);
        textLabel.setOpaque(true);
        add(textLabel, c);
        
        //surrender button
        c.gridx = 0;
        c.gridy = 1;
        c.fill = BOTH;
        c.weighty = 0.05;
        surrender = new JButton("End Game");
        surrender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parent != null) {
                    parent.surrender();
                }
            }
        });
        surrender.setHorizontalAlignment(SwingConstants.CENTER);
        surrender.setVerticalAlignment(SwingConstants.CENTER);
        add(surrender, c);
    }
    
    //use html to format text
    public void setText(String text) {
        String htmlText = text.replace("\n", "<br>");
        textLabel.setText("<html><div style='text-align: center;'>" + htmlText + "</div></html>");
    }
      
    public String getText() {
        return textLabel.getText();
    }
    
    
    private MiniCamelot parent;
    private JLabel textLabel;
    private JButton surrender;
}
