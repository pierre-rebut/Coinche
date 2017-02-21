package jcoinch.server;

import java.util.ArrayList;

import jcoinch.utils.Card;
import jcoinch.utils.Transmition;

public class Board
{
	private final Arbitre			m_arbitre;
	private int						m_nbPlis;
	private int						m_nbElem;
	private final ArrayList<Card>	m_board;
	
	public Board()
	{
		m_arbitre = new Arbitre();
		m_board = new ArrayList<Card>();
		m_nbPlis = 0;
	}
	
	public int checkIfWinner(Card card, Mode mode)
	{
		if (m_nbElem == 4)
		{
			int res = m_arbitre.win_trick(mode, m_board);
			return res;
		}
		return (-1);
	}
	
	public int checkIfEndManche()
	{
		if (m_nbPlis >= 8)
			return 1;
		return 0;
	}
	
	private int putBoardCard(Card card)
	{
		m_board.add(card);
		m_nbElem += 1;
		return 0;
	}
	
	public int putCard(Player pl, Card card, Mode mode)
	{
		int ret = 0;
		if (card == null)
		{
			ret = -1;
			pl.send(new Transmition("ERROR", "Card not in hand"));
		}
		else if (m_board.size() == 0 || m_arbitre.check_coup(mode, pl.getHand(), card, m_board))
			putBoardCard(card);
		else
		{
			pl.send(new Transmition("ERROR", "can't put card on board"));
			return (-1);
		}
		return (ret);
	}
	
	public int getScore(Mode trump)
	{
		int res = m_arbitre.trick_points(trump, m_board);
		m_board.clear();
		m_nbElem = 0;
		m_nbPlis += 1;
		return res;
	}
	
	public void reset()
	{
		m_nbPlis = 0;
	}
	
	public void showBoard(Player pl)
	{
		pl.showBoard(m_board);
	}
}
