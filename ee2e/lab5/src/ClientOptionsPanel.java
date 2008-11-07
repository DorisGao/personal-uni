/*
 *      ClientOptionsFrame.java
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

class ClientOptionsPanel extends JPanel implements ActionListener {

	ClientOptionsPanel(ClientNetwork network) {
		System.out.println("ClientOptionsPanel created");

	// set layout type
		//this.setLayout(new BorderLayout());

	// boxes for packing
		Box vbox = new Box(BoxLayout.Y_AXIS);
		Box hbox = new Box(BoxLayout.X_AXIS);

	// add all the trippy buttons and action listeners
	// Player name box
		JLabel playerLabel = new JLabel("Player Name");
        playerNameField = new JTextField(50);
        playerNameField.addActionListener(this);
	// Connect method as radio buttons
		JLabel connectLabel = new JLabel("Connect to server");
	// connect locally
        localButton = new JRadioButton("Connect Locally");
        localButton.addActionListener(this);
	// connect remotely
        remoteButton = new JRadioButton("Connect Remotely");
        remoteButton.setSelected(true);
        remoteButton.addActionListener(this);
        JLabel remoteIPLabel = new JLabel("Server IP Address");
        remoteIP = new JTextField();
        remoteIP.addActionListener(this);
    // bring the radio buttons together
        radioGroup = new ButtonGroup();
        radioGroup.add(localButton);
        radioGroup.add(remoteButton);
	//OK button
        OKButton = new JButton("OK");
		OKButton.addActionListener(this);

	// bring it all together
		vbox.add(Box.createHorizontalGlue());
		vbox.add(Box.createVerticalGlue());
		vbox.add(playerLabel);
		vbox.add(playerNameField);
        vbox.add(Box.createVerticalGlue());
        vbox.add(connectLabel);
        vbox.add(localButton);
        vbox.add(remoteButton);
        hbox.add(remoteIPLabel);
        hbox.add(remoteIP);
        vbox.add(hbox);
        vbox.add(Box.createVerticalGlue());
		vbox.add(OKButton);

        this.add(vbox);
	}

   	public void actionPerformed(ActionEvent evt)
   	{
		Object source = evt.getSource();

		if(source == playerNameField) {
			player_name = playerNameField.getText();
		}
		else if(source == remoteIP) {
			System.out.println("Remote IP set as " + remoteIP.getText());
		}
		else if(source == localButton) {
			System.out.println("Local Button Selected");
		}
		else if(source == remoteButton) {
			System.out.println("Remote Button Selected");
		}
		else if(source == OKButton) {
			System.out.println("OK Button pressed");
		}
   	}

/**
 * Set options - this needs to be called by the New Game button on ClientGamePanel
 *
 */
   	public boolean SetOptions() {

		if(playerNameField.getText().length() == 0) {
			this.showError("player name");
		}
		else {
			player_name = playerNameField.getText();
		}

		if(localButton.isSelected() == true) {
			server_ip = "127.0.0.1";
		}

		if(remoteButton.isSelected() == true && remoteIP.getText().length() == 0) {
			this.showError("remote IP");
		}
		else if(remoteButton.isSelected() == true && remoteIP.getText().length() !=0) {
			server_ip = remoteIP.getText();
		}
		System.out.println("Player name is: " + player_name);
		System.out.println("Remote IP is: " + server_ip);

		return true;
   	}


	void showError(String type) {
	// display an error dialog cuz something has fubared
		String message = new String();
		String title = new String();
		if(type == "network") {
			message = "Error connecting to the server.\nPlease check your network settings.";
			title = "Network error";
		}
		else if(type == "player name") {
			message = "No player name entered\nPlease enter a player name";
			title = "Player name error";
		}
		else if(type == "remote IP") {
			message = "No remote IP was entered\nPlease enter a remote IP";
			title = "Remote IP error";
		}
		else {
			message = "The error message generator has thrown an error.";
			title = "Error message error!";
		}

		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public String getServer() { return server_ip; }
	public String getPlayer() { return player_name; }

// These are needed else we can't talk to other components
    private ClientNetwork network;
// Other necessaries here
	private JButton OKButton;
	private JTextField playerNameField;
	private JRadioButton localButton;
	private JRadioButton remoteButton;
	private JTextField remoteIP;
	private ButtonGroup radioGroup;
	private String server_ip;
	private String player_name;
}
