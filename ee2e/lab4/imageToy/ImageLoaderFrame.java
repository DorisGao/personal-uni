//package imageToy;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

class ImageLoaderFrame extends JFrame implements ActionListener
{
	public ImageLoaderFrame()
   {
		setTitle("ImageLoader");
      	setSize(800, 600);
      	addWindowListener(new WindowAdapter()
        {  public void windowClosing(WindowEvent e)
            {  System.exit(0);
            }
        } );

	/* The major container */
      	Container contentPane = getContentPane();
      	Box hbox1 = new Box(BoxLayout.LINE_AXIS);
    /* Image containers */
      	game_panel = new TileGrid(this);
      	image_panel = new ImageLoaderPanel(this);
    /* Left hand movable Image */
        JLabel left_box = new JLabel("Game Image");
      	Box vbox_left = new Box(BoxLayout.PAGE_AXIS);
      	vbox_left.add(left_box);
      	vbox_left.add(game_panel);
    /* Right hand reference Image */
      	JLabel right_box = new JLabel("Original Image");
      	Box vbox_right = new Box(BoxLayout.PAGE_AXIS);
      	vbox_right.add(right_box);
      	vbox_right.add(image_panel);
    /* Pull it together */
      	hbox1.add(vbox_left);
      	hbox1.add(vbox_right);
      	contentPane.add(hbox1, "Center");

    /* Game Menu */
    	JMenu gameMenu = new JMenu("Game");
    	loadImage = new JMenuItem("Load Image");
    	loadImage.addActionListener(this);
      gameDifficulty = new JMenuItem("Difficulty");
      gameDifficulty.addActionListener(this);
      imageShuffle = new JMenuItem("Start");
      imageShuffle.addActionListener(this);
    	exitGame = new JMenuItem("Exit");
    	exitGame.addActionListener(this);

		gameMenu.add(loadImage);
      gameMenu.add(gameDifficulty);
      gameMenu.add(imageShuffle);
		gameMenu.add(exitGame);

	/* Bring the menubar together */
       	JMenuBar menuBar = new JMenuBar();
      	menuBar.add(gameMenu);
      	setJMenuBar(menuBar);
   	}

   	public void actionPerformed(ActionEvent evt)
   	{
		Object source = evt.getSource();
	/* Don't edit openItem/exitItem */
      	if (source == loadImage)
      	{
		/* load image file */
			JFileChooser chooser = new JFileChooser();
         	chooser.setCurrentDirectory(new File("."));

         	chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
            {
				public boolean accept(File f)
               	{
					String name = f.getName().toLowerCase();
                  	return name.endsWith(".gif")
                     || name.endsWith(".jpg")
                     || name.endsWith(".jpeg")
                     || f.isDirectory();
               	}

               	public String getDescription()
               	{
					return "Image files";
               	}
            });

         	int r = chooser.showOpenDialog(this);
         	if(r == JFileChooser.APPROVE_OPTION) {
   				name = chooser.getSelectedFile().getAbsolutePath();
               image_panel.loadImage(name);
            }
         }
         else if(source == gameDifficulty)
        {        
          	/* Ask for number of pieces */
        		System.out.println("asking for the number of pieces in a dialog box");

            piecenumber = new TileNumberFrame();
     	   	piecenumber.setVisible(true);
            
         }
         else if(source == imageShuffle)
         {
            if(name != null && piecenumber.getX() != 0 && piecenumber.getY() != 0) {
               System.out.println("Acquired " + piecenumber.getX() + " X number and " + piecenumber.getY() + " Y number");
               game_panel.setGrid(piecenumber.getX(), piecenumber.getY(), name);
               game_panel.repaint();
               repaint();
               System.out.println("This is shuffling the image");
            }
            else
               JOptionPane.showMessageDialog(this, "Please check your image file or difficulty settings.", "Error starting game", JOptionPane.ERROR_MESSAGE);
         }
         else if (source == exitGame)
      		System.exit(-1);
   	}

   	private TileGrid game_panel;
   	private ImageLoaderPanel image_panel;
   	private TileNumberFrame piecenumber;
   	private JMenuItem loadImage;
      private JMenuItem gameDifficulty;
      private JMenuItem imageShuffle;
   	private JMenuItem exitGame;
      private String name;
}