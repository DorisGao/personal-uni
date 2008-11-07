package ginRummy;

/**
 * Card represents a playing card in a deck of cards
 * 
 * @author Ben Francis and Sam Black
 */
class Card implements Comparable
{
	/**
	 * Default constructor sets suit and value as zero
	 */
	public Card()
	{
		suit = 0;
		value = 0;
		name = "";
	}

	/**
	 * Compares two cards, required for array sort. First by suit, then by value.
	 * 
	 * @param 	O 	A card object
	 * @return		0 if cards are the same, +ive integer if O is greater, -ive integer if O is smaller
	 */
	public int compareTo(Object O)
	{
		int a, b;
		Card object = (Card)O;

		a = this.suit - object.suit;
		if(a==0) {
			b = this.value - object.value;
			return b;
		}
		else
			return a;

	}

	/**
	 * Advanced constructor, takes values for suit and value
	 * 
	 * @param 	s 	card suit number
	 * @param	v	card value
	 */
	public Card(int s, int v)
	{
		suit = s;
		value = v;
		name = this.Name();
	}

	/**
	 * Suit number of this card
	 */
	private int suit;
	/**
	 * Value of this card
	 */
	private int value;
	/**
	 * Verbose name for this card
	 */
	private String name;

	/**
	 * Returns the suit number of this card
	 * 
	 * @return		suit number
	 */
	public int GetSuit()
	{
		return this.suit;
	}

	/**
	 * Returns the value of this card
	 * 
	 * @return		card value
	 */
	public int GetValue()
	{
		return this.value;
	}
	
	/**
	 * Assigns this card a verbose name based on its suit and value
	 * 
	 * @return		name of card
	 */
    public String Name()
    {
    	String suit;
    	String value;
    	String name;

    	switch(this.value)
    	{
    	case 1:
    		value = "Ace";
    		break;
    	case 11:
    		value = "Jack";
    		break;
    	case 12:
    		value = "Queen";
    		break;
    	case 13:
    		value = "King";
    		break;
    	default:
    		value = Integer.toString(this.value);
    		break;
    	}

    	switch(this.suit)
    	{
    	case 1:
    		suit = "Clubs";
    		break;
    	case 2:
    		suit = "Diamonds";
    		break;
    	case 3:
    		suit = "Hearts";
    		break;
    	case 4:
    		suit = "Spades";
    		break;
    	default:
    		suit = "Mystery Suit";
    		break;
    	}

    	name = value + " of " + suit;
    	return name;

    }
   
	/**
	 * Returns the name of this card
	 * 
	 * @return		card name
	 */
    public String GetName() {
    	return name;
    }
  
	/**
	 * Tests whether a given card is the same suit as this card
	 * 
	 * @param	c	A card
	 * @return		<code>true</code> if same suit, <code>false</code> if not.
	 */
    private boolean SameSuitAs(Card c)
    {
    	if(this.suit == c.GetSuit())
    		return true;
    	else
    		return false;
    }
    
	/**
	 * Tests whether a given card has the same value as this card
	 * 
	 * @param	c	A card
	 * @return		<code>true</code> if same value, <code>false</code> if not.
	 */
    public boolean SameValueAs(Card c)
    {
    	if(this.value == c.GetValue())
    		return true;
    	else
    		return false;
    }
    
	/**
	 * Tests whether a given card is the same suit and has a value consecutive to the given card
	 * 
	 * @param	c	A card
	 * @return		<code>true</code> if consecutive, <code>false</code> if not.
	 */
    private boolean ConsecutiveTo(Card c)
    {
    	// if greater or smaller than c by one
    	if((this.suit == c.GetSuit()) && (Math.abs(this.value - c.GetValue())==1))
    		return true;
    	else
    		return false;
    }
    
	/**
	 * Tests whether a given card could be used in combination with this card
	 * to create a "run" in Gin Rummy (i.e. the same suit and a consecutive value).
	 * 
	 * @param	c	A card
	 * @return		<code>true</code> if makes suit, <code>false</code> if not.
	 */
    public boolean MakesRunWith(Card c)
    {
    	if(this.SameSuitAs(c) && this.ConsecutiveTo(c))
    		return true;
    	else
    		return false;
    }
}