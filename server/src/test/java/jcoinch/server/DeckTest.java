package jcoinch.server;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import jcoinch.utils.Card;

public class DeckTest {
	@Test
	public void verifyRandomDeck()
	{
		Deck tmp = new Deck();
		ArrayList<Card> lst = tmp.createDeck();
		Assert.assertEquals(lst.size(), 8);
	}
}
