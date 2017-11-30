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
    }
    
    public Move(Cor _piece, Cor _dir) {
        piece = _piece;
        dir = _dir;
    }
    
    public Move(Move m) {
        piece = new Cor(m.piece);
        dir = new Cor(m.dir);
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
        String d = "?";
        ArrayList<String> directions = new ArrayList<>(Arrays.asList("S", "N", "E", "W", "SE", "SW", "NE", "NW"));
        for (int i = 0; i < Constants.compass.size(); ++i) {
            if (Constants.compass.get(i).equals(dir)) {
                d = directions.get(i);
            }
        }
        System.out.println("piece: (" + piece.x + ", " + piece.y + ") " + d);
    }
    
    public Cor piece; //the piece to be moved
    public Cor dir; //the direction to move the piece
}
