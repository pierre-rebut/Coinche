package jcoinch.server;

import java.util.ArrayList;

import com.google.gson.Gson;

import io.netty.channel.Channel;
import jcoinch.utils.Card;
import jcoinch.utils.Transmition;

public class Player {
	
	private final int		m_id;
	private 		String	m_name;
	private final Channel	m_channel;
	private final Team		m_team;
	private ArrayList<Card> m_hand;
	private int				m_score;
	private Integer 		m_belote;
	
	public Player(String name, int id, Channel ch, Team tm)
	{
		m_id = id;
		m_score = 0;
		m_name = name;
		m_channel = ch;
		m_team = tm;
		m_belote = 0;
	}
	
	public void reset()
	{
		m_score = 0;
		m_belote = 0;
	}
	
	public int getBellote()
	{
		return m_belote;
	}
	
	public void addScore(int score)
	{
		m_score += score;
	}
	
	public int getScore()
	{
		return m_score;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public int getId()
	{
		return m_id;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public ArrayList<Card>	getHand()
	{
		return m_hand;
	}
	
	public void setDeck(ArrayList<Card> lstCard)
	{
		m_hand = lstCard;
	}
	
	public void send(Transmition msg)
	{
		m_channel.writeAndFlush(new Gson().toJson(msg, Transmition.class) + "\n");
	}
	
	public Team	getTeam()
	{
		return m_team;
	}
	
	public Channel getChannel()
	{
		return m_channel;
	}
	
	public void exit()
	{
		send(new Transmition("INFO", "Exit game !! bye"));
		m_channel.close();
	}
	
	public void showHand()
	{
		send(new Transmition("SHOW", "HAND", m_hand));
	}
	
	public Card getCard(int idx)
	{
		Card tmp;
		
		tmp = m_hand.get(idx);
		return tmp;
	}
	
	public void removeCard(int idx)
	{
		m_hand.remove(idx);
	}
	
	public void showBoard(ArrayList<Card> board)
	{
		send(new Transmition("SHOW", "BOARD", board));
	}
	
	public int getNbCard()
	{
		return m_hand.size();
	}
	
	public void getBelote(Card.Color color)
	{
		int i = 0;
		for (Card card : m_hand)
			if (card.color == color && (card.value == Card.Value.QUEEN || card.value == Card.Value.KING))
				i++;
		if (i == 2)
			m_belote += 20;
			
	}
	
	public void checkBelote(Mode trump)
	{
		switch(trump)
		{
		case CLUB:
			getBelote(Card.Color.CLUB);
			break;
		case DIAMOND:
			getBelote(Card.Color.DIAMOND);
			break;
		case HEART:
			getBelote(Card.Color.HEART);
			break;
		case SPADE:
			getBelote(Card.Color.SPADE);
			break;
		case ALLTRUMP:
			getBelote(Card.Color.CLUB);
			getBelote(Card.Color.DIAMOND);
			getBelote(Card.Color.HEART);
			getBelote(Card.Color.SPADE);
			break;
		default:
			;
		};
	}
	
}
