/*
 *      ServerNetwork.java
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

// Multithreading inspired by
// http://www.wellho.net/solutions/java-a-multithreaded-server-in-java.html

import java.net.*;
import java.io.*;
import java.util.*;

 /**
  * Server networking class for Snakes and Ladders Game
  */

class ServerNetwork implements Runnable {

	/**
	 * Constructor for daemon thread
	 *
	 * @param server			main server class
	 * @param number_players	number of players to thread for
	 *
	 */
	ServerNetwork(Server server, int number_players) {

		number_sockets = number_players;
		game = server;
		connection_refused = false;
		player = 0;

		System.out.println("ServerNetwork started with " + number_sockets + " sockets");

		ServerSocket ss = null;
		Socket s = null;
		clients = new Vector();

		try {
			ss = new ServerSocket(PORT);
			while ((s=ss.accept())!= null) {

			// Connection received - create a thread
				ServerNetwork now;
				Thread current = new Thread(now = new ServerNetwork(s));
				current.setDaemon(true);
			// Save talker into vector ..
				clients.addElement(now);
			// start the user's thread
				current.start();
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Constructor for client threads
	 *
	 * @param rec	socket to listen for requests
	 *
	 */
	ServerNetwork(Socket rec) {
		soc = rec;
		try {
			out = new PrintWriter(soc.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run method for threads
	 */
	public void run() {

		try {
			exit = false;

			if(player >= number_sockets) {
				connection_refused = true;
				throw new IOException();
			}

			while(true) {
			// reads line 'from client'
				String s = in.readLine();
				String message=null;
			// Abort if null string
				if(s == null) {
					Thread.sleep(200);
					throw new IOException();
				}
				// if the string contains a space, it is likely to be
				// a multi part command
				else if(s.contains(" ")) {
					String[] command = s.split(" ");
					if(command[0].compareTo("CONNECT") == 0) {
					// someone wishes to connect
						if(game.addPlayer()) {
							thread_number = player;
							player++;
							System.out.println("Player " + player + " connected");
							sendToPlayer(thread_number, "201 " + thread_number + " " + number_sockets);
						// if we have enough players, start the game
							if(player == number_sockets) {
								game.startPlay();
							}
						}
						else {
							connection_refused = true;
							out.println("409");
							throw new IOException();
						}
					}
				}
				else if(s.compareTo("ROLL") == 0) {
				// die rolled somewhere
					System.out.println("This is a die roll message");
					System.out.println("thread_number is " + thread_number);
					game.movePlayer(getThreadNumber());
				}
				else if(s.compareTo("DISCONNECT") == 0) {
				// close the socket and decrement players
					exit = true;
					game.removePlayer(getThreadNumber());
				}
				else
				// bad command, not understood
					out.println("400");

				if(exit) {
					clients.removeElement(this);
					try {
						out.close(); // closes needed to terminate connection
						in.close(); // otherwise user's window goes mute
						soc.close();
						player--;
					}
					catch (Exception e) {}
					break;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			if(connection_refused) {
				System.out.println("Connection refused, game full");
				connection_refused = false;
				clients.removeElement(this);
			}
			else {
			// we've lost a player, decrement the number of players
				System.out.println("Player " + player + " quit unexpectedly");
				player--;
				if(player == 0) {
					System.out.println("All players have disconnected, exiting");
					System.exit(0);
				}
			}
		}
	}

	/**
	 * Send messages to all connected clients
	 *
	 * @param message	the message to be sent
	 *
	 */
	public static void sendToAll(String message) {
		int i;
	// transmit a message to all players
		if(message != null) {
			for(i=player; i > 0; i--) {
				System.out.println("Shouting at player " + i + " " + message);
				sendToPlayer((i-1), message);
			}
		}

	}

	/**
	 * Send messages to one client
	 *
	 * @param user		user to send message to
	 * @param message	the message to be sent
	 *
	 */
	public static void sendToPlayer(int user, String message) {
		try {
			Thread.sleep(500);
			ServerNetwork shout = (ServerNetwork)clients.elementAt(user);
			shout.out.println(message);
			shout.out.flush();
			Thread.sleep(500);
		}
		catch(Exception e) { e.printStackTrace(); }
	}

	/**
	 * Returns which thread is currently accessed
	 *
	 * @return thread_number	thread currently accessed
	 *
	 */
	public int getThreadNumber() { return thread_number; }

// private stuff here.
	private final int PORT = 3000; // Port number
	private Socket soc;
	private PrintWriter out=null;
	private BufferedReader in=null;
	private static Vector clients;

	private static Server game;
	private static int number_sockets;
	private static int player;
	private int thread_number;
	private boolean connection_refused;
	private boolean exit;
}
