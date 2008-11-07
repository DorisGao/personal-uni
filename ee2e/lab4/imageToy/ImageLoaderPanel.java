//package imageToy;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

class ImageLoaderPanel extends JPanel
{
	public ImageLoaderPanel(ImageLoaderFrame frame)
	{}

	public void paintComponent(Graphics g)
   	{
		super.paintComponent(g);
      	if (image != null)
      	{
         	g.drawImage(image, 0, 0, null);
 		}
   	}

   	public void loadImage(String name)
   	{
		Image loadedImage = Toolkit.getDefaultToolkit().getImage(name);
      	MediaTracker tracker = new MediaTracker(this);
      	tracker.addImage(loadedImage, 0);
      	try { tracker.waitForID(0); }
      	catch (InterruptedException e) {}
        image = new BufferedImage(loadedImage.getWidth(null),
                loadedImage.getHeight(null), BufferedImage.TYPE_INT_RGB);

	/*
	 * We'll need to throw an exception if the image is too wide to fit the
	 * window, else we'll get clipping.
	 * This should be fairly easy, 2x image.getWidth() + 25px or something
	 */
        System.out.println("image " + name + " is " + image.getWidth() + "x" + image.getHeight() + " pixels");

      	Graphics2D g2=image.createGraphics();
       	g2.drawImage(loadedImage, 0, 0, null);

       	repaint();
   	}

   	private BufferedImage image;
}