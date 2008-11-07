//package imageToy;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

public class TileGrid extends JPanel{

   public TileGrid(ImageLoaderFrame frame)
   { }

   public void setGrid(int x, int y, String image_path){
      
      int i, j;
/* New array of tiles */
      tiles = new Tile[x][y];

/* Read the Image chunks into the array as tiles */
      int tile_height, tile_width;
      for(i=0; i<x; i++)
         for(j=0; j<y; j++) {
        	tile_width = loadImage(image_path).getWidth()/x;
        	tile_height = loadImage(image_path).getHeight()/y;

            tiles[i][j] = new Tile((i+1), (j+1), loadImage(image_path).getSubimage((i*tile_width), (j*tile_width), tile_width, tile_height));
            //tiles[i][j].draw(tile_width, tile_height);
         }
      tiles[0][0].draw(0, 0);
   }

   public BufferedImage loadImage(String name){
		
      Image loadedImage = Toolkit.getDefaultToolkit().getImage(name);
     	MediaTracker tracker = new MediaTracker(this);
     	tracker.addImage(loadedImage, 0);
     	try { tracker.waitForID(0); }
     	catch (InterruptedException e) {}
      image = new BufferedImage(loadedImage.getWidth(null),
               loadedImage.getHeight(null), BufferedImage.TYPE_INT_RGB);

      return image;
   }

   private BufferedImage image;
   private Tile[][] tiles;

}
