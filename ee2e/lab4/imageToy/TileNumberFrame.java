//package imageToy;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;


class TileNumberFrame extends JDialog implements ChangeListener, ActionListener
{
	public TileNumberFrame()
	{
		setSize(255, 125);
		setTitle("Number of Pieces");
      	setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Container content = getContentPane();

		okButton = new JButton("OK");
		okButton.addActionListener(this);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		xPieces = new JSlider(1, 10);
		xPieces.addChangeListener(this);
		xPieces.setSnapToTicks(true);

		yPieces = new JSlider(1, 10);
		yPieces.addChangeListener(this);
		yPieces.setSnapToTicks(true);

		xPiecesLabel = new JLabel("Number of horizontal pieces: " + xPieces.getValue());
		yPiecesLabel = new JLabel("Number of vertical pieces: " + yPieces.getValue());

		Box slidervbox = new Box(BoxLayout.PAGE_AXIS);
		slidervbox.add(xPiecesLabel);
		slidervbox.add(xPieces);
		slidervbox.add(yPiecesLabel);
		slidervbox.add(yPieces);

		Box buttonhbox = new Box(BoxLayout.LINE_AXIS);
		buttonhbox.add(okButton);
		buttonhbox.add(cancelButton);

		content.add(slidervbox, "Center");
		content.add(buttonhbox, "South");

	}


   	public void stateChanged(ChangeEvent event)
   	{
		Object source = event.getSource();

		if(source == xPieces)
		{
			System.out.println("number of xPieces is " + xPieces.getValue());
			xPiecesLabel.setText("Number of horizontal pieces: " + xPieces.getValue());
			xPiecesLabel.repaint();
		}
		else if(source == yPieces)
		{
			System.out.println("Number of yPieces is " + yPieces.getValue());
			yPiecesLabel.setText("Number of vertical pieces: " + yPieces.getValue());
			yPiecesLabel.repaint();
		}
		else
		{
			System.out.println("Error in stateChanged");
		}
   	}

	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();

		if (source == okButton)
		{
			System.out.println("Ok pressed");
			x = xPieces.getValue();
			y = yPieces.getValue();
			System.out.println("X is " + x + " Y is " + y);
			this.dispose();
		}
		else if(source == cancelButton)
		{
			System.out.println("Cancel pressed");
			this.dispose();
		}
		else
			System.out.println("Error in actionPerformed");
	}

	public int getX() { return x;}
	public int getY() { return y;}

	private JButton okButton;
	private JButton cancelButton;
	private JLabel xPiecesLabel;
	private JLabel yPiecesLabel;
	private JSlider xPieces;
	private JSlider yPieces;
	private int x;
	private int y;

}
