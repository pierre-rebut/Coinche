package jcoinch.server;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import jcoinch.utils.Card;

public class PlayerTest {
	
	@Test
	public void getBeloteTest()
	{
		Player pl = new Player("test", 0, null, null);
		ArrayList<Card> hand = new ArrayList<Card>();
		
		hand.add(new Card(1, 5));
		hand.add(new Card(1, 6));
		pl.setDeck(hand);
		pl.getBelote(Card.Color.CLUB);
		Assert.assertEquals(20, pl.getBellote());
	}

}
