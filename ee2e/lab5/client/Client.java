/*
 *      Client.java
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
 *      
 *      @author Ben Francis
 *      @version 1.0
 */

 /**
  * Main controller class for Snakes and Ladders client
  */
 public class Client {

 	/**
 	 * Constructor
 	 */
 	 Client() {
 		gui = new Gui(this);
 		gui.setStatusBar("Please enter a name and server IP address to connect.");
		network = new Network(this);
	}

 	 /**
 	  * main method simply instantiates class
 	  * 
 	  */
 	public static void main(String[] args) {
		Client client = new Client();
	}
 	
 	/**
 	 * Wrapper to connect in Network class, used to abstract away networking
 	 * 
 	 * @param server		IP address of game server
 	 * @param playerName	Name of current player entered by user
 	 * @return true if successful, false if not
 	 */
 	public boolean connect(String server, String playerName) {
 		String s = server;
 		String p = playerName;
 		String result;
 		System.out.println("Connecting...");
 		//Print 201 or 400
		network.connect(s,p);
		while(Listen());
		return true;
 	}
 	
 	
 	/**
 	 * Wrapper to disconnect in Network class
 	 *
 	 */
 	public void disconnect() {
 		network.disconnect();
 	}
 	
 	/**
 	 * Sends roll message to server, tells client to listen until listening no longer required
 	 *
 	 * @return true when finished
 	 */
 	public boolean roll() {
 		//System.out.println("client roll called");
 		network.sendRoll();
		while(Listen());
		return true;
 	}
 	
 	/**
 	 * Returns whether or not currently connected
 	 * 
 	 * @return true if connected, false if not
 	 */
 	public boolean isConnected() {
 		return connected;
 	}
 	
 	/**
 	 * Parses messages received from server
 	 * 
 	 * @param cmd Command received
 	 * @return true if need to listen again, false if not
 	 */
 	private boolean parseCommand(String cmd) {
 		//parse commands!
 		//System.out.println("parseCommand say: " + cmd);
 		String[] elements = cmd.split(" ");

 		// CONNECTED (201)
 		if(elements[0].compareTo("201") == 0) {
 			//System.out.println(elements[0] + "/" + elements[1] + "/" + elements[2]);
 			gui.setStatusBar("Connected.");
 			
 			player_no = Integer.valueOf(elements[1]);
 			System.out.println("I am player " + player_no);
 			
 			int numberOfPlayers = Integer.valueOf(elements[2]);
 			System.out.println("There are " + numberOfPlayers + " players");
 			
 			/* This code causes crashes, probably because of recursive events 
 			
 			//set number of players		
 			gui.setNumberOfPlayers(2);
 			
 			int playerNumber = Integer.valueOf(elements[1]);
 			//Place counters on board
 			int i;
 			for(i=0; i<numberOfPlayers; i++) {
 				gui.moveCounter(i, 1);
 			}*/
 			return true;
 		}
 				
 		// ROLLED
 		else if(elements[0].compareTo("ROLLED") == 0) {
 			gui.setStatusBar("A " + elements[1] + " was rolled.");
 			return true;
 		}
 		
 		// TURN
 		else if(elements[0].compareTo("TURN") == 0) {
 			if(Integer.valueOf(elements[1]) == player_no) {
 				gui.setStatusBar("Your turn");
 				return false;
 			}
 			else {
 				gui.setStatusBar("Player " + elements[1] + "'s turn.");
 				return true;
 			}
 		}
 		
 		// MOVED
 		else if(elements[0].compareTo("MOVED") == 0) {
 			gui.setStatusBar("Player " + elements[1] + " moved " + elements[2]);
 			return true;
 		}
 		
 		//anything else
 		else {
 			gui.setStatusBar("The server returned something weird. DON'T PANIC!");
 			return false;
 		}
 	}
 	
 	/**
 	 * Wrapper to network listener, calls parsing of commands received.
 	 *
 	 * @return true if need to listen again, false if not
 	 */
 	public boolean Listen() {
 		System.out.println("Listening...");
 		//System.out.println(network.Listen());
 		return(parseCommand(network.Listen()));
 	}

	/**
	 * Graphical user interface
	 */
 	private Gui gui;
 	
	/**
	 * Network
	 */
 	private Network network;
 	
 	/**
 	 * Specifies whether client currently connected to server
 	 */
	private boolean connected = false;
	
	/**
	 * Specifies client's player number in game for self identification
	 */
	private int player_no;
}
