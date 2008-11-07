//package imageToy;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

/**
 * A class which represents a Tile in the puzzle which can be moved around the TileGrid
 * 
 * @author Ben Francis
 */
class Tile  extends JPanel {
	
	/**
	 * Constructor used for gap
	 * 
	 * @param i	x co-ordinate of Tile on TileGrid
	 * @param j	y co-ordinate of Tile on TileGrid
	 */
	public Tile(int i, int j) {
		x = i;
		y = j;
	}
	
	/**
	 * Constructor used for all other Tiles
	 * 
	 * @param i	x co-ordinate of Tile on TileGrid
	 * @param j	y co-ordinate of Tile on TileGrid
	 * @param tileimage	sub-image of the main image
	 */
	public Tile(int i, int j, BufferedImage tileimage) {
		x = i;
		y = j;
		image = tileimage;
	}
	
	public void paintComponent(Graphics g)
   	{
		super.paintComponent(g);
      	if (image != null)
      	{
         	g.drawImage(image, 0, 0, null);
 		}
   	}
	
	/**
	 * Returns the x co-ordinate of this Tile
	 * 
	 * @return the x co-ordinate of this Tile
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the y co-ordinate of this Tile
	 * 
	 * @return the y co-ordinate of this Tile
	 */
	public int getY() {
		return y;
	}
	
	public void draw(int w, int h) {

      	Graphics2D g2=image.createGraphics();
       	g2.drawImage(image, 0, 0, null);

       	repaint();
	}
	
private int x;
private int y;
private BufferedImage image;

}