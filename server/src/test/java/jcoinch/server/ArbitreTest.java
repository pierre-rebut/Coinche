package jcoinch.server;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import jcoinch.utils.Card;

public class ArbitreTest {
	@Test
	public void better_trump_in_hand()
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card(3, 2));
		hand.add(new Card(2, 0));
		hand.add(new Card(1, 3));
		hand.add(new Card(3, 4));
		ArrayList<Card> trick = new ArrayList<Card>();
		trick.add(new Card(3, 0));
		trick.add(new Card(3, 3));
		trick.add(new Card(3, 1));
		Card card = new Card(3,5);
		Arbitre arbitre = new Arbitre();
		boolean test = arbitre.check_coup(Mode.DIAMOND, hand, card, trick);
		Assert.assertEquals(false, test);
	}
	
	@Test
	public void trump_in_hand()
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card(3, 2));
		hand.add(new Card(2, 0));
		hand.add(new Card(1, 3));
		hand.add(new Card(3, 4));
		ArrayList<Card> trick = new ArrayList<Card>();
		trick.add(new Card(3, 0));
		trick.add(new Card(3, 3));
		trick.add(new Card(3, 1));
		Card card = new Card(1,5);
		Arbitre arbitre = new Arbitre();
		boolean test = arbitre.check_coup(Mode.DIAMOND, hand, card, trick);
		Assert.assertEquals(false, test);
	}
	@Test
	public void have_color_in_hand()
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card(3, 2));
		hand.add(new Card(2, 0));
		hand.add(new Card(1, 3));
		hand.add(new Card(3, 4));
		ArrayList<Card> trick = new ArrayList<Card>();
		trick.add(new Card(2, 0));
		trick.add(new Card(2, 3));
		trick.add(new Card(2, 1));
		Card card = new Card(1,5);
		Arbitre arbitre = new Arbitre();
		Assert.assertEquals(false, arbitre.check_coup(Mode.DIAMOND, hand, card, trick));
		Assert.assertEquals(false, arbitre.check_coup(Mode.NOTRUMP, hand, card, trick));
		Assert.assertEquals(false, arbitre.check_coup(Mode.ALLTRUMP, hand, card, trick));
	}
	@Test
	public void have_trump_to_cut()
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card(3, 2));
		hand.add(new Card(3, 0));
		hand.add(new Card(1, 3));
		hand.add(new Card(3, 4));
		ArrayList<Card> trick = new ArrayList<Card>();
		trick.add(new Card(2, 0));
		trick.add(new Card(2, 3));
		trick.add(new Card(2, 1));
		Card card = new Card(1,5);
		Arbitre arbitre = new Arbitre();
		boolean test = arbitre.check_coup(Mode.DIAMOND, hand, card, trick);
		Assert.assertEquals(false, test);
	}	
	@Test
	public void overcut()
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card(3, 2));
		hand.add(new Card(3, 0));
		hand.add(new Card(1, 3));
		hand.add(new Card(3, 4));
		ArrayList<Card> trick = new ArrayList<Card>();
		trick.add(new Card(2, 0));
		trick.add(new Card(3, 6));
		trick.add(new Card(2, 1));
		Card card = new Card(3,5);
		Arbitre arbitre = new Arbitre();
		Assert.assertEquals(false, arbitre.check_coup(Mode.DIAMOND, hand, card, trick));
	}
	@Test
	public void overcut_all_trump()
	{
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card(3, 4));
		hand.add(new Card(3, 0));
		hand.add(new Card(1, 3));
		hand.add(new Card(3, 6));
		ArrayList<Card> trick = new ArrayList<Card>();
		trick.add(new Card(3, 2));
		trick.add(new Card(2, 0));
		trick.add(new Card(2, 1));
		Card card = new Card(3,5);
		Arbitre arbitre = new Arbitre();
		Assert.assertEquals(false, arbitre.check_coup(Mode.ALLTRUMP, hand, card, trick));
	}
	@Test
	public void check_win()
	{
		ArrayList<Card> trick = new ArrayList<Card>();
		trick.add(0, new Card(2, 2));
		trick.add(1, new Card(2, 0));
		trick.add(2, new Card(2, 1));
		trick.add(3, new Card(2, 3));
		Arbitre arbitre = new Arbitre();
		Assert.assertEquals(new Integer(3), arbitre.win_trick(Mode.DIAMOND, trick));
		Assert.assertEquals(new Integer(0), arbitre.win_trick(Mode.ALLTRUMP, trick));
		Assert.assertEquals(new Integer(3), arbitre.win_trick(Mode.NOTRUMP, trick));
		trick.set(2, new Card(3, 2));
		trick.set(3, new Card(3, 6));
		Assert.assertEquals(new Integer(2), arbitre.win_trick(Mode.DIAMOND, trick));
		Assert.assertEquals(new Integer(0), arbitre.win_trick(Mode.ALLTRUMP, trick));
		Assert.assertEquals(new Integer(0), arbitre.win_trick(Mode.NOTRUMP, trick));
		trick.set(0, new Card(3, 1));
		trick.set(1, new Card(3, 4));
		Assert.assertEquals(new Integer(1), arbitre.win_trick(Mode.ALLTRUMP, trick));
	}
	@Test
	public void count_point()
	{
		ArrayList<Card> trick = new ArrayList<Card>();
		trick.add(0, new Card(2, 2));
		trick.add(1, new Card(2, 6));
		trick.add(2, new Card(2, 7));
		trick.add(3, new Card(2, 3));
		Arbitre arbitre = new Arbitre();
		Assert.assertEquals(new Integer(25), arbitre.trick_points(Mode.DIAMOND, trick));
		Assert.assertEquals(new Integer(23), arbitre.trick_points(Mode.ALLTRUMP, trick));
		Assert.assertEquals(new Integer(33), arbitre.trick_points(Mode.NOTRUMP, trick));
		trick.set(2, new Card(3, 7));
		trick.set(3, new Card(3, 2));
		Assert.assertEquals(new Integer(29), arbitre.trick_points(Mode.DIAMOND, trick));
		Assert.assertEquals(new Integer(27), arbitre.trick_points(Mode.ALLTRUMP, trick));
		Assert.assertEquals(new Integer(23), arbitre.trick_points(Mode.NOTRUMP, trick));
	}
}
