package ginRummy;
import java.util.Random;
import java.util.LinkedList;

/**
 * Deck represents a standard deck of playing cards with all the cards you'd expect except jokers
 * Deck is a special type of {@link Pile} which can be cut and shuffled
 * 
 * @author Ben Francis and Sam Black
 * @see Pile
 */
class Deck extends Pile
{
	/**
	 * Constructor creates a Pile and adds four suits of 13 cards
	 */
	public Deck()
	{
		super();
		int suit;
		int value;

		for(suit=1; suit<=4; suit++) {
			for(value=1; value<=13; value++) {
				this.add(new Card(suit,value));
			}
		}

	}

	/**
	 * Prints the whole deck to the screen for testing purposes
	 */
	public void PrintDeck()
	{
		int size = this.size();
		int i;
		for(i=0;i<size;i++) {
			//there has to be an easier way than this
			Card temp = new Card();
			temp = (Card)this.get(i);
			System.out.print(temp.Name() + "\n");
		}
	}

	/**
	 * Randomly re-arranges the {@link Card}s in the deck
	 */
	public void Shuffle()
	{
		int size, random, i;
		Random generator = new Random();
		Card temp = new Card();
		size = this.size();

		for(i=0;i<100;i++) {
			//take random card
			random = generator.nextInt(size);
			temp = (Card)this.remove(random);
			//put it back in a random position
			random = generator.nextInt(size);
			this.add(random,temp);
		}

	}
	/**
	 * Splits the deck at a random position and inverts the two halves.
	 * This method could be more elegant if we could edit linked list pointers directly.
	 */
	public void Cut()
	{
		int cutpoint, size, i;
		Random generator = new Random();
		LinkedList temp = new LinkedList();
		size = this.size();

		//pick a random place to cut
		cutpoint = generator.nextInt(size);
		//remove all of the cards up to the cut point
		for(i=0;i<=cutpoint;i++)
			temp.add(this.removeFirst());
		//add removed cards to the end of the deck
		this.addAll(temp);
	}

}
