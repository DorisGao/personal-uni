/*
 *      Network.java
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

import java.io.*;
import java.net.*;

class Network {

	/**
	 * Constructor takes instantiating client object as a parameter
	 * 
	 * @param client
	 */
	Network(Client client) {
		System.out.println("Network started.");
	}

	/**
	 * Creates network socket and sends CONNECT message to server
	 * 
	 * @param server	IP address of server
	 * @param playerName	Name of player
	 * @return	any information that needs sending back to Client
	 */
	public String connect(String server, String playerName) {
		try{
			// Try to connect to server at IP address/port combination
			Socket soc=new Socket(server,PORT);
			// Now get the 'streams' - you use these to send messages
			out=new PrintWriter(soc.getOutputStream(),true);
			in=new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out.println("CONNECT " + playerName);
			return "";
		}
		catch(Exception e){
			//e.printStackTrace(); // Displays Error if things go wrong....
			System.out.println("Exception during connect");
			return "";
		}
	}
	
	/*
	 * Sends an arbitary string to the server
	 * 
	 * @param	msg	message to be sent
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Sends disconnect message to server
	 */
	public void disconnect() {
		out.println("DISCONNECT");
	}
	
	/**
	 * Listen for messages from server until one is received
	 * 
	 * @return	the message received
	 */
	public String Listen() {
		String s=null;
		try {
			while(true) {
				s = in.readLine();
				if(s == null) {
					System.out.println("Listen heard NULL.");
					return "";
				}
				else {
					System.out.println("Server said: " + s);
					return s;
				}
					
			}
		}
		catch(Exception e) {
			System.out.println("Exception while listening.");
			return "";
		}
	}
	
	/**
	 * Sends a roll message to the server
	 * 
	 * @return	any data that needs passing back to Client
	 */
	public String sendRoll() {
		System.out.println("Rolled");
		out.println("ROLL");
		return "";
	}
	
	/**
	 * Ping server (for testing purposes)
	 */
	public void ping() {
		out.println("PING");
	}

	/**
	 * Port number to use
	 */
	private final int PORT=3000;
	
	/**
	 * messages to be sent out
	 */
	private PrintWriter out=null;
	
	/**
	 * Messages received from server
	 */
	private BufferedReader in=null;

}
