package ginRummy;

import junit.framework.TestCase;

/**
 * Unit test for Card class
 * 
 * @author Ben Francis
 */
public class CardTest extends TestCase {

	private Card card;
	private Card card1;
	private Card card2;
	private Card card3;
	private Card card4;

	/**
	 * Test method for {@link ginRummy.Card#compareTo(java.lang.Object)}.
	 */
	public void testCompareTo() {
		Card card = new Card(1,1);
		Card card1 = new Card(1,1);
		Card card2 = new Card(1,2);
		assertEquals(card.compareTo(card1), 0);
		assertEquals(card.compareTo(card2), -1);
		assertEquals(card2.compareTo(card), 1);
	}

	/**
	 * Test method for {@link ginRummy.Card#Card(int, int)}.
	 */
	public void testCard() {
		Card card = new Card(1,1);
		assertEquals(1,card.GetSuit());
		assertEquals(1,card.GetValue());
	}

	/**
	 * Test method for {@link ginRummy.Card#GetSuit()}.
	 */
	public void testGetSuit() {
		Card card = new Card(2,3);
		assertEquals(card.GetSuit(), 2);
	}

	/**
	 * Test method for {@link ginRummy.Card#GetValue()}.
	 */
	public void testGetValue() {
		Card card = new Card(4,5);
		assertEquals(card.GetValue(), 5);
	}

	/**
	 * Test method for {@link ginRummy.Card#Name()}.
	 */
	public void testName() {
		Card card = new Card(1,1);
		assertEquals(card.Name(), "Ace of Clubs");
	}

	/**
	 * Test method for {@link ginRummy.Card#GetName()}.
	 */
	public void testGetName() {
		Card card = new Card(1,1);
		assertEquals(card.GetName(), "Ace of Clubs");
	}

	/**
	 * Test method for {@link ginRummy.Card#SameValueAs(ginRummy.Card)}.
	 */
	public void testSameValueAs() {
		Card card1 = new Card(1,1);
		Card card2 = new Card(1,1);
		assertEquals(card1.SameValueAs(card2), true);
		
		Card card3 = new Card(2,2);
		Card card4 = new Card(2,3);
		assertEquals(card3.SameValueAs(card4), false);
		
	}

	/**
	 * Test method for {@link ginRummy.Card#MakesRunWith(ginRummy.Card)}.
	 */
	public void testMakesRunWith() {
		Card card1 = new Card(1,1);
		Card card2 = new Card(1,2);
		assertEquals(card1.MakesRunWith(card2), true);
		
		Card card3 = new Card(2,2);
		Card card4 = new Card(3,2);
		assertEquals(card3.MakesRunWith(card4), false);
	}

}
