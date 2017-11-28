/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.Objects;

/**
 *
 * @author Matthew Laikhram
 */
public class Move {
    public Move(Cor _piece, Cor _dir) {
        piece = _piece;
        dir = _dir;
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
        System.out.println("piece: (" + piece.x + ", " + piece.y + ") to (" + dir.x + ", " + dir.y + ")");
    }
    
    public Cor piece; //the piece to be moved
    public Cor dir; //the direction to move the piece
}
