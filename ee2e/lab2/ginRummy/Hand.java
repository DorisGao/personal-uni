package ginRummy;
import java.util.Arrays;

/**
 * Hand represents a hand of {@link Card}s in a card game
 * Hand is a special type of {@link Pile} which can be tested as a winning hand in Gin Rummy
 * 
 * @author Ben Francis and Sam Black
 * @see Pile
 */
class Hand extends Pile
{
	/**
	 * The constructor simply inherits from its superclass, {@link Pile}
	 */
	public Hand()
	{
		super();
	}

	/**
	 * Prints the names of all the {@link Card}s in this hand in an ordered list
	 */
	public void PrintHand()
	{
		int size = this.size();
		int i;
		for(i=0;i<size;i++) {
			//there has to be an easier way than this
			Card temp = new Card();
			temp = (Card)this.get(i);
			System.out.print((i+1) + ". " + temp.GetName() + "\n");
		}
	}
	
	/**
	 * Tests this Hand to determine whether it could be a winning hand in Gin Rummy. 
	 * See documentation for algorithm design.
	 * 
	 * @return		<code>true</code> if a winning hand, <code>false</false> if not.
	 */
	public boolean WinningHand()
	{
		int i, runs=0, books=0;
		boolean found_group_of_four=false;
		
		//make hand an array
		cards = new Card[7];
		System.arraycopy(this.toArray(), 0, cards, 0, this.size());

		//sort hand by suit, then by value
		Arrays.sort(cards);
		
		//search for runs
		int run_length=1;
		for(i=1; i<7; i++) {
			if(cards[i].MakesRunWith(cards[i-1])) {
				run_length++;
				//System.out.print((cards[i].Name())+" makes run with "+cards[i-1].Name()+"\n");
				if(run_length==3)
					runs++;							//found a run
				if(run_length==4) {
					run_length = 1;					//run reached maximum length
					found_group_of_four=true;		//note that found a group of four
				}
			}
			else
				run_length = 1;
		}
		
		//search for books
		int book_size, j;
		checked = new boolean[14];						//to keep track of checked values
		for(i=0;i<7;i++) {
			book_size=1;
			if(!(checked[cards[i].GetValue()])) { 		//ensure this value hasn't been checked for already
				checked[cards[i].GetValue()]=true;		//note this value as having being checked
				for(j=0;j<7;j++) {						//loop through hand
					if((cards[i].SameValueAs(cards[j]) && i!=j)) {
						book_size++;					//found another card with same value
						if(book_size==3)
							books++;					//found a book
						if(book_size==4) {
							book_size=0;				//book reached maximum length
							found_group_of_four=true;	//note that group of four has been found
						}
					}
					
				}
			}
		}
		
		//System.out.print("found "+runs+" runs and "+books+" books\n");
		
		//look for 2 or more groups
		if(((runs+books)>=2 && found_group_of_four))
			return true;
		else
			return false;

	}
	/**
	 * An array of Cards to hold the Hand when it is converted into an array for sorting
	 */
	private Card[] cards;
	
	/**
	 * An array which keeps track of which card values have been checked for potential books
	 */
	private boolean[] checked;
	
}