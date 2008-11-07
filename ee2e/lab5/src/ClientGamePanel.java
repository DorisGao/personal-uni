/*
 *      ClientGameFrame.java
 *
 *      Copyright 2007 Sam Black <samwwwblack@lapwing.org>
 * 		Copyright 2007 Ben Francis <ben@tola.me.uk> 
 * 
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.net.URL;

class ClientGamePanel extends JPanel implements ActionListener {
	
	ClientGamePanel(ClientNetwork network) {
		System.out.println("ClientGamePanel created");
		
	// set layout type
		this.setLayout(new BorderLayout());
		
		gui = new SnakesAndLaddersGUI();

		Box buttonvbox = new Box(BoxLayout.PAGE_AXIS);
		
		JLabel dice_box = new JLabel("Dice");
	// add image of the dice here, based on server return value
		dice_icon = new JLabel(createImageIcon("dice_base.gif", "Dice Image"));
		rollButton = new JButton("Roll Dice");
		rollButton.addActionListener(this);	

	// button vbox first
		buttonvbox.add(Box.createVerticalGlue());
		buttonvbox.add(dice_box);
		buttonvbox.add(dice_icon);
		buttonvbox.add(rollButton);
		buttonvbox.add(Box.createVerticalGlue());
	// add hbox to the game panel
		this.add(gui);
		this.add(buttonvbox, "East");

	}

   	public void actionPerformed(ActionEvent evt)
   	{
		Object source = evt.getSource();

      	if (source == rollButton)
      	{
      	// this will call a method in ClientNetwork to notify the server
      	// of the "dice roll".
      	// the server will come back with the number rolled, and the dice image
      	// will be updated
			System.out.println("rollButton pressed");
      	}
   	}
   	
	public void set_player_number(int number) {
		gui.setNumberOfPlayers(number);
	}
	
	public void move_piece(int player, int place) {
		gui.setPosition(player, place);
	}
	
	// Returns an ImageIcon, or null if the path was invalid.
	// From the Java tutorial pages
	// http://java.sun.com/docs/books/tutorial/uiswing/components/icon.html
	protected static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = ClientGamePanel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		}
		else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
    
// These are needed else we can't talk to other components
    private ClientNetwork network;
// Other necessaries here
	private SnakesAndLaddersGUI gui;
	private JLabel dice_icon;
	private JButton rollButton;
}
