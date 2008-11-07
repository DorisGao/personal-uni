/*
 *      ClientNetwork.java
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

import java.io.*;
import java.net.*;

class ClientNetwork {

	ClientNetwork() {
		try{
		// Try to connect to server at IP address/port combination
			Socket soc=new Socket("127.0.0.1",PORT);
		// Now get the 'streams' - you use these to send messages
			out=new PrintWriter(soc.getOutputStream(),true);
			in=new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out.println("Hello!\n");
			//out.close();
			System.out.println(in.readLine());
		}
		catch(Exception e){
			e.printStackTrace(); // Displays Error if things go wrong....
		}
		System.out.println("ClientNetwork created");
	}

	boolean connect(String server, String player_name) {
	// connect to the server at server, with the name player_name
		return true;
	}

	boolean disconnect(String server, String player_name) {
	// disconnect from the server at server
		return true;
	}

	private final int PORT=3000; // Port number
	private PrintWriter out=null;
	private BufferedReader in=null;

}
