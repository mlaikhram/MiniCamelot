/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

/**
 *
 * @author Matthew Laikhram
 */
public class Move {
    public Move(Cor _piece, Cor _dir) {
        piece = _piece;
        dir = _dir;
    }
    
    public void print() {
        System.out.println("piece: (" + piece.x + ", " + piece.y + ") to (" + dir.x + ", " + dir.y + ")");
    }
    
    public Cor piece; //the piece to be moved
    public Cor dir; //the direction to move the piece
}
