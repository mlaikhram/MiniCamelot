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
public class Cor {
    public Cor(int _x, int _y) {
        x = _x;
        y = _y;         
    }
    
    public Cor(Cor c) {
       x = c.x;
       y = c.y;
    }
    
    //add 2 cors together
    public Cor add(Cor c) {
        Cor ans = new Cor(x + c.x, y + c.y);
        return ans;
    }
    
    //implementation for hashmap
    public int hashCode() {
        return x * 100 + y;
    }
    
    //implementation for hashmap
    public boolean equals(Cor c) {
        return x == c.x && y == c.y;
    }
    
    public int x;
    public int y;
}
