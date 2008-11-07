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

import java.net.*;
import java.io.*;

class ServerNetwork implements Runnable {

	ServerNetwork(SnakesAndLaddersServer game) {
		try{
			ss = new ServerSocket(PORT);
		}
		catch(Exception e){
			e.printStackTrace(); // Displays Error if things go wrong....
		}
	}
	
	public void run() {
		System.out.println("This has started a threaded socket listener");
		try {
			Socket soc=ss.accept();		// Waits for client to connect
		// Now get the 'streams'
			out = new PrintWriter(soc.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			
			System.out.print("Server thread started\n");
			while(true) { // Infinite loop
				String s=in.readLine(); // reads line 'from client'
				if(s==null)break; // Abort if null string
				System.out.println(s); // Prints it out
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
// private stuff here.
// why the hell do we need private stuff
// waste of time and effort if you ask me ;-)
	private final int PORT = 3000; // Port number
	private PrintWriter out;
	private BufferedReader in;
	private ServerSocket ss;
	private SnakesAndLaddersServer game;
}

