/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Matthew Laikhram
 */
public class Move {
    
    public Move() {
        piece = new Cor(0, 0);
        dir = new Cor(0, 0);
        chain = null;
    }
    
    public Move(Cor _piece, Cor _dir) {
        piece = _piece;
        dir = _dir;
        chain = null;
    }
    
    public Move(Move m) {
        piece = new Cor(m.piece);
        dir = new Cor(m.dir);
        if (m.chain == null) {
            chain = null;
        }
        else {
            chain = new Move(m.chain);
        }
    }
    //creates a Move by linking the two moves together
    public Move(Move m1, Move m2) {
        piece = new Cor(m1.piece);
        dir = new Cor(m1.dir);
        if (m1.chain != null) {
            chain = new Move(m1.chain);
        }
        
        //get to the end of the chain and link m2 to it
        Move m = this;
        while (m.chain != null) {
            m = m.chain;
        }
        m.chain = new Move(m2);
    }
    
    //implementation for hashmap
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Move other = (Move) obj;
        if (!this.piece.equals(other.piece)) {
            return false;
        }
        if (!this.dir.equals(other.dir)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.piece);
        hash = 19 * hash + Objects.hashCode(this.dir);
        return hash;
    }
    
    
    public void print() {

        ArrayList<String> directions = new ArrayList<>(Arrays.asList("S", "N", "E", "W", "SE", "SW", "NE", "NW"));
        
        System.out.print("piece: (" + piece.x + ", " + piece.y + ")");
        
        Move c = this;
        while (c != null) {
            String d = "X";
            for (int i = 0; i < Constants.compass.size(); ++i) {
                if (Constants.compass.get(i).equals(c.dir)) {
                    d = directions.get(i);
                    break;
                }
            }
            System.out.print(" " + d);
            c = c.chain;
        }
        System.out.println();
    }
    
    
    public static void main(String[] args) {
        Move m = new Move(new Cor(4, 5), Constants.N);
        m.chain = new Move (new Cor(4, 4), Constants.SW);
        m.chain.chain = new Move(new Cor(3, 5), Constants.S);
        
        Move m2 = new Move(new Cor(3, 6), Constants.E);
        m2.chain = new Move(new Cor(4, 6), Constants.NE);
        
        Move m3 = new Move(m, m2);
        m3.print();
    }
    
    
    public Cor piece; //the piece to be moved
    public Cor dir; //the direction to move the piece
    public Move chain;
}
