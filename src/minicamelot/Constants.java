/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author Matthew Laikhram
 */
public class Constants {
    public static final int ROWS = 14;
    public static final int COLS = 8;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final Cor S = new Cor(0, 1);
    public static final Cor N = new Cor(0, -1);
    public static final Cor E = new Cor(1, 0);
    public static final Cor W = new Cor(-1, 0);
    public static final Cor SE = new Cor(1, 1);
    public static final Cor SW = new Cor(-1, 1);
    public static final Cor NE = new Cor(1, -1);
    public static final Cor NW = new Cor(-1, -1);
    public static final LinkedList<Cor> compass = new LinkedList<>(Arrays.asList(S, N, E, W, SE, SW, NE, NW));
    
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
    
}
