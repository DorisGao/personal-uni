/*
 *      Gui.java
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

/**
 * Graphical User Interface for Snakes and Ladders game
 */
class Gui extends JFrame implements ActionListener {

	/**
	 * Constructor
	 *
	 * @param	client	Accepts instantiating Client object as argument
	 */
	Gui(Client client) {
		
		this.client = client;
		
		System.out.println("GUI started.");
		
		//Configure frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(600,600);
		this.setTitle("Snakes and Ladders");
		this.setVisible(true);

		//Panel to contain GUI
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		//Connect Box
		Box connectBox = new Box(BoxLayout.X_AXIS);
		
		JLabel playerNameLabel = new JLabel("Player Name:");
		connectBox.add(playerNameLabel);
		
        playerNameField = new JTextField(10);
        playerNameField.setText("Anon");
        connectBox.add(playerNameField);
        
        JLabel ipAddressLabel = new JLabel("Server IP address:");
        connectBox.add(ipAddressLabel);
        
        ipAddressField = new JTextField(10);
        ipAddressField.setText("127.0.0.1");
        ipAddressField.disable();
        connectBox.add(ipAddressField);
        
        localCheckbox = new JCheckBox("local", true);
        localCheckbox.addActionListener(this);
        connectBox.add(localCheckbox);

        connectButton = new JButton("Connect");
		connectButton.addActionListener(this);
		connectBox.add(connectButton);
		
		//Board
		Box boardBox = new Box(BoxLayout.PAGE_AXIS);
		
		board = new SnakesAndLaddersGUI();
		
		boardBox.add(board);
		
		//Roll button and status bar
		Box rollBox = new Box(BoxLayout.Y_AXIS);
		
		rollButton = new JButton("Roll");
		rollButton.addActionListener(this);
		rollBox.add(rollButton);
		
		statusBar = new JLabel("");
		rollBox.add(statusBar);
		
		
		//Add boxes to panel
		panel.add(connectBox, BorderLayout.NORTH);
		panel.add(boardBox, BorderLayout.CENTER);
		panel.add(rollBox, BorderLayout.SOUTH);
		
		this.getContentPane().add(panel);
		this.show();
		
		//demonstration that board works (real code broken)
		setNumberOfPlayers(2);
		moveCounter(0,1);
		moveCounter(1,1);

	}

	/**
	 * Validates user input for connection to server
	 * 
	 * @return	true if input valid, false if input invalid
	 */
	private boolean validateInput() {
		
		if(playerNameField.getText().length()==0 || ipAddressField.getText().length()==0)
			return false;
		else
			return true;
	}
	
	/**
	 * Display a string of text on the status bar
	 * 
	 * @param msg	Message to be displayed
	 */
	public void setStatusBar(String msg) {
		statusBar.setText(msg);
	}
	
	/**
	 * Disable connect box so can't reconnect
	 * 
	 */
	private void disableConnectBox() {
		connectButton.setText("Disconnect");
		playerNameField.disable();
		ipAddressField.disable();
		localCheckbox.disable();
	}
	
	/**
	 * Enable roll button
	 */
	public void enableRoll() {
		rollButton.enable();
	}
	
	/**
	 * Disable roll button
	 */
	public void disableRoll() {
		rollButton.disable();
	}
	
	/** Set the number of players in the game
	 * 
	 * @param no_of_players Number of Players
	 */
	public void setNumberOfPlayers(int no_of_players) {
		System.out.println("Trying to set " + no_of_players + "players");
		board.setNumberOfPlayers(no_of_players);
	}
	
	/**
	 * Move counter on board (wrapper to setPosition in SnakesAndLaddersGUI)
	 * 
	 * @param player_no Player Number
	 * @param position  Position to move to
	 */
	public void moveCounter(int player_no, int position) {
		board.setPosition(player_no, position);
	}

	/**
	 * Implementation of actionPerformed function for actionListener,
	 * calls client methods based on event source
	 * 
	 * @param	evt	an event
	 */
	public void actionPerformed(ActionEvent evt)
   	{
		Object source = evt.getSource();
		
		//CONNECT
		if(source == connectButton) {
			/*setNumberOfPlayers(2);
			moveCounter(0,1);
			moveCounter(1,1);*/
			if(!client.isConnected()) {
				//System.out.println("Connect button pressed.");
				if(validateInput()) {
					disableConnectBox();
					client.connect(ipAddressField.getText(), playerNameField.getText());
				}
				else {
					setStatusBar("Invalid name or IP address.");
				}	
			}
			else {
				client.disconnect();
			}
			
      	}
		
		// Set whether local or remote
		else if(source == localCheckbox) {
			if(localCheckbox.isSelected()) {
				System.out.println("Set to local.");
				ipAddressField.setText("127.0.0.1");
				ipAddressField.disable();
			}
			else {
				System.out.println("Set to remote.");
				ipAddressField.setText("0.0.0.0");
				ipAddressField.enable();
			}
		}
		
		//ROLL
		else if(source == rollButton) {
			System.out.println("Roll button pressed.");
			client.roll();
		}
		else
			System.out.println("An unknown action occured.");
	}
	
	/**
	 * Provided snakes and ladders board class
	 */
	private SnakesAndLaddersGUI board;
	
	private JPanel panel;
	private JButton connectButton;
	private JTextField playerNameField;
	private JTextField ipAddressField;
	private JCheckBox localCheckbox;
	private JButton rollButton;
	private JLabel statusBar;
	
	/**
	 * controller class passed in constructor
	 */
	private Client client;

}
