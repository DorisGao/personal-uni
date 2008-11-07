/*
 * @(#)Player.java  6.04.02
 *
 * A base class for a draughts player
 *
 * Dr. Mike Spann
 *
 *
*/
import java.util.*;

public class Player
{

	public Player() {

	}

	public int movePiece(DraughtBoard board, char color)
	{
		int direction, i, j, taken=0, moved=0, random;
		Random generator = new Random();
		random = generator.nextInt(2);

		System.out.println("Start: Random is " + random + " color is " + color);

		//scan for takes
		for(i=0;(i<8 && taken!=1);i++) {
			for(j=0;(j<8 && taken!=1);j++) {
			      System.out.println("Take: i is " + i + " and j is " + j);
				switch(canTake(board, i, j, color))
				{
					case 1:
						//take left
						//if take was successful, taken = 1
						System.out.println("Take left");
						if(color=='w' && board.moveWhite(i, j, 'l') == 0)
							taken = 1;
						else if(color=='b' && board.moveBlack(i, j, 'l') == 0)
							taken = 1;
						else
							taken = 0;
						break;
					case 2:
						//take right
						//if take was successful, taken = 1
						System.out.println("Take right");
						if(color=='w' && board.moveWhite(i, j, 'r') == 0)
							taken = 1;
						else if(color=='b' && board.moveBlack(i, j, 'r') == 0)
							taken = 1;
						else
							taken = 0;
						break;
					case 3:
						//take in a random direction
						random = generator.nextInt(2);
						System.out.println("Random for take is " + random);
						if(random == 0) {
							if(color == 'w' && board.moveWhite(i,j,'l') == 0) {
								taken = 1;
								System.out.println("Random White left take");
							}
							else if(color == 'b' && board.moveBlack(i,j,'l') == 0) {
								taken = 1;
								System.out.println("Random Black left take");
							}
							else
								taken = 0;
						}
						else if(random == 1) {
							if(color == 'w' && board.moveWhite(i,j,'r') == 0) {
								taken = 1;
								System.out.println("Random White right take");
							}
							else if(color == 'b' && board.moveBlack(i,j,'r') == 0) {
								taken = 1;
								System.out.println("Random Black right take");
							}
							else
								taken = 0;
						}

						break;

					default:
                                            taken = 0;
					    break;
				}
			}
		}
                if(taken == 0) {
		  for(i=0;(i<8 && moved!=1);i++) {
			for(j=0;(j<8 && moved!=1);j++) {
			      System.out.println("Move: i is " + i + " and j is " + j);
			      switch(canMove(board, i, j, color))
                               {
				    case 1:
					  //move left
					  System.out.println("Move left");
					  if(color=='w' && board.moveWhite(i, j, 'l') == 0)
						moved = 1;
					  else if(color=='b' && board.moveBlack(i, j, 'l') == 0)
						moved = 1;
					  else
						moved = 0;
				    break;

				    case 2:
					  //move right
					  System.out.println("Move right");
					  if(color=='w' && board.moveWhite(i, j, 'r') == 0)
						moved = 1;
					  else if(color=='b' && board.moveBlack(i, j, 'r') == 0)
						moved = 1;
					  else
						moved = 0;
				    break;

				    case 3:
					  //move in a random direction
					  random = generator.nextInt(2);
					  System.out.println("Random for move is " + random);
					  if(random == 0) {
						if(color == 'w' && board.moveWhite(i,j,'l') == 0) {
						      moved = 1;
						      System.out.println("Random White left move");
						}
						else if(color == 'b' && board.moveBlack(i,j,'l') == 0) {
						      moved = 1;
						      System.out.println("Random Black left move");
						}
						else
						      moved = 0;
					  }
					  else if(random == 1) {
						if(color == 'w' && board.moveWhite(i,j,'r') == 0) {
						      moved = 1;
						      System.out.println("Random White right move");
						}
						else if(color == 'b' && board.moveBlack(i,j,'r') == 0) {
										moved = 1;
										System.out.println("Random Black right move");
						}
						else
						      moved = 0;
					  }
					  break;

					  default:
						//otherthing
						moved = 0;
					  break;
				    }

				}
			}
		}


	  	return 0;
	}

	public int canTake(DraughtBoard board, int x, int y, char color)
	{
		int direction, value=0;
		//find direction of play
		if(color == 'w')
			direction = 1;
		else if(color == 'b')
			direction = -1;
		else
			direction = 0;

		if(board.getPiece(x,y)==color) {
			if((direction == 1 && y == 7) || (direction == -1 && y == 0)) {
				value = 0;
			}
			else if(x>1 && board.getPiece(x-1,y+direction)!=color && board.getPiece(x-1,y+direction)!= 'e') {
				value = 1;
			}
			else if(x<6 && board.getPiece(x+1,y+direction)!=color && board.getPiece(x+1,y+direction)!= 'e') {
				value = 2;
			}
			else
				value = 3;
		}
		return value;
	}

	public int canMove(DraughtBoard board, int x, int y, char color)
	{
		int direction, value=0;
		//find direction of play
		if(color == 'w')
			direction = 1;
		else if(color == 'b')
			direction = -1;
		else
			direction = 0;

		if(board.getPiece(x,y)==color) {
			if((direction == 1 && y == 7) || (direction == -1 && y == 0)) {
				value = 0;
			}
			else if(x>1 && board.getPiece(x-1,y+direction)== 'e') {
				value = 1;
			}
			else if(x<6 && board.getPiece(x+1,y+direction)== 'e') {
				value = 2;
			}
			else
				value = 3;
		}
		return value;
	}



}
