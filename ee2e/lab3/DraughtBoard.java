/*
 * @(#)DraughtBoard.java  6.04.02
 *
 * Represents the state of the draught board
 *
 * Dr. Mike Spann
 *
 *
*/


public class DraughtBoard
{

	public static final char BLACK='b';	//represents a black piece
	public static final char WHITE='w';	//represents a white piece
	public static final char EMPTY='e';	//board position is empty

	public static final char LEFT='l';	//a left move
	public static final char RIGHT='r';	//a right move

	public DraughtBoard()
	{

	 	/***
		* Initialize the board layout
	  	***/

	  	board=new char[8][8];

	  	for (int x=0; x<8; x++)
	      for (int y=0; y<8; y++)
	        board[x][y]=EMPTY;

	  	setBoard();

	}

	public void setBoard()
	{

    	/***
        * Initialize the board layout
        ***/

        whitePiecesLeft=12;

        blackPiecesLeft=12;

        whiteScore=0;

        blackScore=0;

        for (int x=0; x<8; x++)
          for (int y=0; y<8; y++)
            board[x][y]=EMPTY;

	  	board[0][0]=board[2][0]=board[4][0]=board[6][0]=WHITE;
	  	board[1][1]=board[3][1]=board[5][1]=board[7][1]=WHITE;
	  	board[0][2]=board[2][2]=board[4][2]=board[6][2]=WHITE;

	  	board[1][7]=board[3][7]=board[5][7]=board[7][7]=BLACK;
	  	board[0][6]=board[2][6]=board[4][6]=board[6][6]=BLACK;
	  	board[1][5]=board[3][5]=board[5][5]=board[7][5]=BLACK;

	}

	public char getPiece(int x, int y) {return board[x][y];}

	public int getWhiteScore() { return whiteScore;}

	public int getBlackScore() { return blackScore;}

	public int getWhitePiecesLeft() { return whitePiecesLeft;}

	public int getBlackPiecesLeft() { return blackPiecesLeft;}


	public int moveBlack(int x, int y, char direction)
	{

	  	/***
	  	* Moves a black piece in a specified direction
	  	***/
	  	if(board[x][y]==BLACK)
	  		if(direction == 'l' && x!=0)
	  		{
				if(board[x-1][y-1]==EMPTY)
				{
	  				board[x-1][y-1]=BLACK;
	  				board[x][y]=EMPTY;
	  				return 0;
				}
				else if(x>1 && y>2 && board[x-1][y-1]==WHITE && board[x-2][y-2]==EMPTY)
				{
					board[x-2][y-2]=BLACK;
					board[x][y]=EMPTY;
					board[x-1][y-1]=EMPTY;
					whitePiecesLeft--;
					return 0;
				}
				else
					return 1;
			}
	  		else if(direction == 'r' && x!=7)
	  		{
				if(board[x+1][y-1]==EMPTY)
				{
	  				board[x+1][y-1]=BLACK;
	  				board[x][y]=EMPTY;
	  				return 0;
				}
				else if(x<6 && y>2 && board[x+1][y-1]==WHITE && board[x+2][y-2]==EMPTY)
				{
					board[x+2][y-2]=BLACK;
					board[x][y]=EMPTY;
					board[x+1][y-1]=EMPTY;
					whitePiecesLeft--;
					return 0;
				}
				else
					return 1;
			}
	  		else
	  		{
	  			System.out.println("Error cannot move black piece");
	  			return 1;
			}
	  	else
	  	{
	  		System.out.println("Error, black piece not there");
	  		return 1;
		}

	}


	public int moveWhite(int x, int y, char direction)
	{

	  	/***
	  	* Moves a white piece in a specified direction
	  	***/

	  	if(board[x][y]==WHITE)
	  		if(direction == 'l' && x!=0)
	  		{
				if(board[x-1][y+1]==EMPTY)
				{
	  				board[x-1][y+1]=WHITE;
	  				board[x][y]=EMPTY;
	  				return 0;
				}
				else if(x>1 && y<6 && board[x-1][y+1]==BLACK && board[x-2][y+2]==EMPTY)
				{
					board[x-2][y+2]=WHITE;
					board[x][y]=EMPTY;
					board[x-1][y+1]=EMPTY;
					blackPiecesLeft--;
					return 0;
				}
				else
					return 1;
			}
	  		else if(direction == 'r' && x!=7)
	  		{
				if(board[x+1][y+1]==EMPTY)
				{
	  				board[x+1][y+1]=WHITE;
	  				board[x][y]=EMPTY;
	  				return 0;
				}
				else if(x<6 && y<6 && board[x+1][y+1]==BLACK && board[x+2][y+2]==EMPTY)
				{
					board[x+2][y+2]=WHITE;
					board[x][y]=EMPTY;
					board[x+1][y+1]=EMPTY;
					blackPiecesLeft--;
					return 0;
				}
				else
					return 1;
			}
	  		else
	  		{
	  			System.out.println("Error cannot move white piece");
	  			return 1;
			}
	  	else
	  	{
	  		System.out.println("Error, piece not there");
	  		return 1;
		}

	}

	private char board[][];

	private int whitePiecesLeft=12;

   	private int blackPiecesLeft=12;

    private int whiteScore=0;       // The number of white pieces over the black line

   	private int blackScore=0;       // The number of black pieces over the white line
}

