package jcoinch.utils;

import java.util.ArrayList;

public class Transmition
{
	public String			m_type;
	public String			m_cmd;
	public ArrayList<Card>	m_cards;
	
	public Transmition()
	{
	}
	
	public Transmition(String type, String cmd)
	{
		m_type = type;
		m_cmd = cmd;
	}
	
	public Transmition(String type, String cmd, ArrayList<Card> cards)
	{
		m_type = type;
		m_cmd = cmd;
		m_cards = cards;
	}
}
