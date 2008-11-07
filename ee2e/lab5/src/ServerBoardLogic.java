/*
 *      ServerBoardLogic.java
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
import java.util.Random;

class ServerBoardLogic {
	
	ServerBoardLogic(ServerNetwork network) {
		int i;
		// this is the constructor
		players = new int[5];
		snakes_ladders = new int[101];
		
		for(i = 0; i < 5; i++)
			players[i] = 0;
		for(i = 0; i <= 100; i++)
			snakes_ladders[i] = 0;
	}
	
	public boolean addPlayer() {
		
		if(number_players == 5)
			return false;
		else {
			players[number_players] = 1;
			number_players++;
			return true;
		}
	}
	
	public boolean removePlayer(int player) {
		
		if(number_players == 0)
			return false;
		else {
			players[player] = 0;
			number_players--;
			return true;
		}
	}
	
	public void movePlayer(int player) {
		
		int value, dice;
	// calculate if we are near the end of the board
	// we aren't normal play resumes
		value = 100 - players[player];
		if(value > 6) {
		// calculate new position	
			players[player] = players[player] + dice_roll();
		// tell the clients the new place	
		// this should tell the network section to do something
		
		// now check what (if any) snakes/ladders modifiers are needed	
			if(snakes_ladders[players[player]] != 0) {
				players[player] = snakes_ladders[players[player]];
			// update all clients
			}
		}
	// we are near the end of the board, special rules apply
		else
		{
			if(dice_roll() == value) {
				players[player] = 100;
				System.out.println("Game ended as player " + player + " has won the game!");
			}
			else if(dice_roll() < value) {
			// calculate new position	
				players[player] = players[player] + dice_roll();
			// tell the clients the new place	
			// this should tell the network section to do something
			
			// now check what (if any) snakes/ladders modifiers are needed	
				if(snakes_ladders[players[player]] != 0) {
					players[player] = snakes_ladders[players[player]];
				// update all clients
				}
			}
			else {
			// send a message out to clients that the player missed
				System.out.println("The dice roll was too high to win");
			}
		}
	}
	
	int dice_roll() {
		
		Random generator = new Random();
		return generator.nextInt();
		
	}
	
// variable stuff that we need
	private int[] players;
	private int[] snakes_ladders;
	private int number_players=0;
}
