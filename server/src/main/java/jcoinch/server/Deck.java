package jcoinch.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import jcoinch.utils.Card;

public class Deck {
	private ArrayList<Card>		m_lstCard;
	private LinkedList<Card>	m_deck;
 
	public Deck()
	{
		m_lstCard = new ArrayList<Card>();
		for (int color = 0; color < 4; color++)
		{
			for (int val = 0; val < 8; val++ )
				m_lstCard.add(new Card(color, val));
		}
		initDeck();
	}
	
	public void initDeck()
	{
		m_deck = new LinkedList<Card>();
		for (int i = 0; i < m_lstCard.size(); i++)
			m_deck.add(m_lstCard.get(i));
		Collections.shuffle(m_deck);
	}
	
	public ArrayList<Card> createDeck()
	{
		ArrayList<Card> ret = new ArrayList<Card>();
		for (int i = 0; i < 8; i++)
			ret.add(m_deck.pollFirst());
		return ret;
	}
}
