package ginRummy;
import java.util.*;

/**
 * Game is the main class to simulate a Gin Rummy card game of up to 4 players.
 * The game has a text based user interface for a fully interactive game, all players
 * can see all other players' hands.
 * 
 * @version 1.0
 * @author Ben Francis and Sam Black
 * @see Pile
 */
class Game
{
	/**
	 * Constructor creates a new {@link Deck}, shuffles it and deals the 
	 * {@link Card}s out to a given number of player's {@link Hand}s, 
	 * creates a discard {@link Pile}, removes the top card from Deck and places
	 * it on the discard pile.
	 */
	public Game()
	{
		//create deck
		deck = new Deck();
		//shuffle deck
		deck.Shuffle();
		//deal
		this.Deal(deck);
		//create discard pile, show top card
		discard_pile = new Pile();
		discard_pile.add(deck.removeFirst());

	}

	/**
	 * Prompts the user for a number of players and then adds 7
	 * {@link Card}s to each player's {@link Hand}.
	 * 
	 * @param	deck	A shuffled deck of cards to be dealt
	 * @return			<code>0</code> upon successful dealing of cards.
	 */
	public int Deal(Deck deck)
	{
		int i,j;
		do {
			System.out.print("Number of players:");
			Scanner in = new Scanner(System.in);
			String n = in.nextLine();
			number_of_players = Integer.valueOf(n);
		} while(number_of_players>4 || number_of_players<1); //check there are 1 - 4 players

		hands = new Hand[number_of_players];
		for(i=0;i<number_of_players;i++) {
			hands[i] = new Hand();
			for(j=0;j<7;j++) {
				hands[i].add(deck.removeFirst());
			}
		}

		return 0;
	}

	/**
	 * Plays the game of Gin Rummy.
	 * 
	 * Iterates through each player (until someone wins) and allows them to choose to pick up a {@link Card}
	 * from the discard {@link Pile} or a card from the {@link Deck}. Then lets them choose which card to put down.
	 * Once the end of the deck is reached, moves the cards in the discard pile to the deck, shuffles it
	 * and puts the top card of the deck on the discard pile.
	 * After each turn, checks to see if the current player's hand is a winning hand.
	 */
	public void Play()
	{
		int player = 0, card_number;
		while(player!=99) {

			System.out.print("\nPLAYER " + (player+1) + "\n");
			hands[player].PrintHand();
			Card top = new Card();
			top = (Card)discard_pile.getLast();
			System.out.print("\nTop card: " + top.Name() + "\n");
			System.out.print("\nTake top card or from deck?\n");
			System.out.print("T (Top Card), D (Deck) : ");
			Scanner in = new Scanner(System.in);

			String cmd = "";
			while(!(cmd.equals("T")||cmd.equals("D"))) {
				cmd = in.nextLine();
				if(cmd.equals("T")){
					hands[player].add(discard_pile.removeLast());
				}
				else if(cmd.equals("D")) {
					hands[player].add(deck.removeFirst());
				}
				else
					System.out.print("Please enter T or D: ");
			}
			System.out.print("\nYour cards:\n");
			hands[player].PrintHand();

			card_number = 99;
			while(card_number<1 || card_number>8) {
				System.out.print("\nWhich card would you like to put down? ");
				card_number = in.nextInt();
			}

			discard_pile.add(hands[player].remove(card_number-1));

			//if end of deck reached, discard pile made into deck and shuffled
			if(deck.size()==0) {
				deck.addAll(discard_pile);
				deck.Shuffle();
				discard_pile.clear();
				discard_pile.add(deck.removeFirst());
			}

			hands[player].PrintHand();
			
			// test for a winning hand
			if(hands[player].WinningHand()) {
				System.out.print("\nPLAYER "+(player+1)+" WINS!");
				player = 99;
			}
			else {
				//next player's turn
				if(player<(number_of_players-1))
					player++;
				else
					player = 0;
			}		

		}


	}
	
	/**
	 * Creates a new game of Gin Rummy and plays the game
	 * 
	 * @see #Play()
	 */
	public static void main(String[] args)
    {

		Game game = new Game();
		game.Play();
    }

	/**
	 * A deck of playing cards for this game
	 */
	private Deck deck;
	
	/**
	 * A {@link Pile} for {@link Card}s players discard from their {@link Hand}s
	 */
	private Pile discard_pile;
	
	/**
	 * An array of all the {@link Hand}s in the game.
	 */
	private Hand[] hands;
	
	/**
	 * The number of players playing this game.
	 */
	private int number_of_players;
}