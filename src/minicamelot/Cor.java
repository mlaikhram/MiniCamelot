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
    
    //finds midpoint between 2 cors
    public Cor mid(Cor c) {
        Cor sum = add(c);
        Cor ans = new Cor(sum.x / 2, sum.y / 2);
        return ans;
    }
    
    //implementation for hashmap
    @Override
    public int hashCode() {
        return x * 100 + y;
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
        final Cor other = (Cor) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
    
    public void print() {
        System.out.println("(" + x + ", " + y + ")");
    }
    
    public int x;
    public int y;
}
