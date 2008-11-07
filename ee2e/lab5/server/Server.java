/*
 *      Server.java
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
import java.util.*;

 /**
  * Main server class for Snakes and Ladders Game
  */

public class Server {

	/**
	 * Constructor
	 *
	 * @param args	integer value of server arguments
	 *
	 */
	Server(int args) {
		int i;
	// this is the constructor
		players = new int[5];
		snakes_ladders = new int[101];
	// set variables
		number_players = 0;
		counter = 1;
		playgame = false;

		for(i = 0; i < 5; i++)
			players[i] = 0;

		for(i = 0; i <= 100; i++)
			snakes_ladders[i] = 0;

	// will need to setup board here, ie box 10 is a ladder to 24 etc
	//	snakes_ladders[10] = 24;
		snakes_ladders[1] = 38;
		snakes_ladders[6] = 16;
		snakes_ladders[11] = 49;
		snakes_ladders[14] = 4;
		snakes_ladders[21] = 60;
		snakes_ladders[24] = 87;
		snakes_ladders[31] = 9;
		snakes_ladders[35] = 54;
		snakes_ladders[44] = 26;
		snakes_ladders[51] = 67;
		snakes_ladders[56] = 53;
		snakes_ladders[62] = 19;
		snakes_ladders[64] = 42;
		snakes_ladders[73] = 92;
		snakes_ladders[78] = 100;
		snakes_ladders[84] = 28;
		snakes_ladders[91] = 71;
		snakes_ladders[95] = 75;
		snakes_ladders[98] = 80;

	// start server networking
		network = new ServerNetwork(this, args);
	}

	/**
	 * adds a player to the game
	 *
	 * @return success or fail
	 */
	public boolean addPlayer() {

		if(number_players == 4)
			return false;
		else {
			players[number_players] = 1;
			number_players++;
			return true;
		}
	}

	/**
	 * removes a player from the game
	 *
	 * @return success or fail
	 */
	public boolean removePlayer(int player) {

		if(number_players == 0)
			return false;
		else {
			players[player] = 0;
			number_players--;
			return true;
		}
	}

	/**
	 * Moves a player along the snakes and ladders board
	 *
	 * @param player	the player to be moved
	 */
	public void movePlayer(int player) {

		if(playgame && (counter == (player+1))) {
			System.out.println("Server: player " + player + " " + players[player]);
			int value, dice;
		// calculate if we are near the end of the board
			value = 100 - players[player];
		// Roll the die
			dice = dice_roll();
		// Notify all of the die roll
			network.sendToAll("ROLLED " + dice);
		// we aren't so normal play resumes
			if(value > 6) {
			// calculate new position
				players[player] = players[player] + dice;
			// tell the clients the new place
				network.sendToAll("MOVED " + player + " " + players[player]);

			// now check what (if any) snakes/ladders modifiers are needed
				if(snakes_ladders[players[player]] != 0) {
					players[player] = snakes_ladders[players[player]];
				// update all clients
					network.sendToAll("MOVED " + player + " " + players[player]);
				}
			}
		// we are near the end of the board, special rules apply
			else
			{
				if(dice == value) {
					players[player] = 100;
					network.sendToAll("MOVED " + player + " " + players[player]);
					System.out.println("Game ended as player " + player + " has won the game!");
					network.sendToAll("WON " + player);
				// we shouldn't be able to play as a player has won!
					playgame = false;
				}
				else if(dice < value) {
				// calculate new position
					players[player] = players[player] + dice;
				// tell the clients the new place
					network.sendToAll("MOVED " + player + " " + players[player]);

				// now check what (if any) snakes/ladders modifiers are needed
					if(snakes_ladders[players[player]] != 0) {
						players[player] = snakes_ladders[players[player]];
					// update all clients
						network.sendToAll("MOVED " + player + " " + players[player]);
					}
				}
				else {
				// send a message out to clients that the player missed
					System.out.println("The dice roll was too high to win");
				}
			}
		// increment the counter
			if(counter == number_players)
				counter=1;
			else
				counter++;
		// state whos turn it is
			network.sendToAll("TURN " + ((player + 1) % number_players));
		}
		else if(!playgame) {
			System.out.println("Crazy foo is trying to roll the die before the games started!");
			network.sendToPlayer(player, "405");
		}
		else {
		// its not their turn, bugger off!
			System.out.println("Ooops, unknown error occured!");
			network.sendToPlayer(player, "405");
		}
	}

	/**
	 * Rolls the dice
	 *
	 * @return (Math.abs(roll) + 1)	value of the dice roll
	 */
	private int dice_roll() {

		int roll;

		Random generator = new Random();
		roll = generator.nextInt() % 5;

		return (Math.abs(roll) + 1);

	}

	/**
	 * Sets the startgame flag
	 *
	 */
	public void startPlay() {
		playgame = true;
		network.sendToAll("TURN 0");
	}

	/**
	 * Main method
	 *
	 * @param a	arguments passed at command line
	 */
	public static void main(String[] a){

		int args, i;

	// turn the args component into an int
		if(a.length > 0 && (Integer.valueOf(a[0]) > 1)) {
			Server game_server = new Server(Integer.valueOf(a[0]));
		}
		else {
			System.out.println("You must specify how many players there are for the game");
			System.out.println("ie: java Server 3");
			System.out.println("For 2 to 5 players");
		}

	}
// private things
	private int[] players;
	private int[] snakes_ladders;
	private int number_players;
	private boolean playgame;
	private static int counter;
	private ServerNetwork network;
}
