/*
 *      ClientFrame.java
 *
 *		Copyright 2007 Sam Black <samwwwblack@lapwing.org>
 *		Copyright 2007 Ben Francis <ben@tola.me.uk>
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

class ClientFrame extends JFrame implements ActionListener {

	ClientFrame() {

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(600,600);
		this.setTitle("Snakes and Ladders");
	// setup the game panel
		game = new ClientGamePanel(sockets);
	// setup the options panel
		options = new ClientOptionsPanel(sockets);

	// splash screen buttons
		gameButton = new JButton("New Game");
		gameButton.addActionListener(this);
		optionsButton = new JButton("Options");
		optionsButton.addActionListener(this);
		backButton = new JButton("Back");
		backButton.addActionListener(this);
		disconnectButton = new JButton("Disconnect");
		disconnectButton.addActionListener(this);

	// show the splash screen
		this.splash_screen();

	}

   	public void actionPerformed(ActionEvent evt)
   	{
		Object source = evt.getSource();

		if(source == gameButton) {

			if(!options.SetOptions())
				System.out.print("Setting options failed");
			else {
				//connect
				System.out.println("This is connecting");
			}
			System.out.println("Start game called");
		// hide the game/options
			hbox.setVisible(false);
		// add the game panel and make visible
			this.getContentPane().add(game);
			game.setVisible(true);
		// add the disconnect button and make visible
			this.getContentPane().add(disconnectButton, "South");
			disconnectButton.setVisible(true);
		}
		else if(source == optionsButton) {
			System.out.println("Options called");
		// hide the game/options
			hbox.setVisible(false);
		// add the options panel and make visible
			this.getContentPane().add(options);
			options.setVisible(true);
		}
		else if(source == backButton) {
			System.out.println("Back called");
			this.splash_screen();
		}
		else if(source == disconnectButton) {
			System.out.println("Disconnect called");
		// send disconnect signal to the server
			sockets.disconnect(options.getServer(), options.getPlayer());
			this.splash_screen();
		}
   	}

	void splash_screen() {

	// hide all the panels and back button
		game.setVisible(false);
		options.setVisible(false);
		backButton.setVisible(false);
		disconnectButton.setVisible(false);

	// boxes to hold it all in
		Box vbox = new Box(BoxLayout.PAGE_AXIS);
		hbox = new Box(BoxLayout.LINE_AXIS);
	// bring it together
	// add some "glue" (whitespace) to the top and bottom
		vbox.add(Box.createVerticalGlue());
		vbox.add(gameButton);
		vbox.add(optionsButton);
		vbox.add(Box.createVerticalGlue());
	// now stick the vbox into the hbox with glue
	// this makes it centered in the window
		hbox.add(Box.createHorizontalGlue());
		hbox.add(vbox);
		hbox.add(Box.createHorizontalGlue());
	// add the menu to the window
		this.getContentPane().add(hbox);
	}

	private ClientNetwork sockets;
	private ClientGamePanel game;
	private ClientOptionsPanel options;
	private Box hbox;

	private JButton gameButton;
	private JButton optionsButton;
	private JButton backButton;
	private JButton disconnectButton;

}
