package jcoinch.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jcoinch.utils.Transmition;

public class Contrat {
	private Team				m_team;
	private Player 				m_player;
	private int					m_contrat;
	private boolean				m_coinche;
	private boolean				m_surcoinche;
	private int					m_passes;
	private Map<String, Integer> Contrats;

	public Contrat()
	{
		m_passes = 0;
		m_contrat = 0;
		Map<String, Integer> Contrats = new HashMap<String, Integer>();
	      Contrats.put("80", 82);
	      Contrats.put("90", 90);
	      Contrats.put("100", 100);
	      Contrats.put("110", 110);
	      Contrats.put("120", 120);
	      Contrats.put("130", 130);
	      Contrats.put("140", 140);
	      Contrats.put("150", 150);
	      Contrats.put("160", 160);
	      Contrats.put("capot", 250);
	      Contrats.put("generale", 500);
	      this.Contrats = Contrats;
	}
	
	private int verifContrat(String contrat)
	{
		for (Map.Entry<String, Integer> entry : Contrats.entrySet())
		{
			if (entry.getKey().equalsIgnoreCase(contrat) == true)
				return (entry.getValue());
		}
		return (-1);
	}
	
	public int getContrat()
	{
		return m_contrat;
	}
	
	public Player getPlayerContrat()
	{
		return m_player;
	}
	
	public void reset()
	{
		m_player = null;
		m_passes = 0;
		m_contrat = 0;
		m_coinche = false;
		m_surcoinche = false;
		m_team = null;
	}

	public int checkContrat(Player pl, String contrat)
	{
		int ret = 0;
		if (contrat.toLowerCase().equals("passe"))
		{
			m_passes += 1;
			ret = -1;
		}
		else if (m_contrat > 0 && m_coinche == false && contrat.equalsIgnoreCase("coinche"))
		{
			if (pl.getTeam() != m_team)
			{
				m_coinche = true;
				ret = -2;
			}
			else
				pl.send(new Transmition("ERROR", "Only ennemie can coinche"));
		}
		else if (m_coinche == true && m_surcoinche == false && contrat.equalsIgnoreCase("surcoinche"))
		{
			if (pl.getTeam() == m_team)
			{
				m_surcoinche = true;
				ret = -3;
			}
			else
				pl.send(new Transmition("ERROR", "Only ennemie can surcoinche"));
		}
		else if (m_coinche != true)
		{
			int i = verifContrat(contrat);
			if (i != -1 && i > m_contrat)
			{
				m_player = pl;
				m_team = pl.getTeam();
				m_contrat = i;
				m_passes = 0;
				ret = -4;
			}
			else
				pl.send(new Transmition("ERROR", "incorect contrat"));
		}
		else
			pl.send(new Transmition("ERROR", "incorect arg"));
		if (m_passes >= 4 && m_contrat == 0)
			return (2);
		else if (m_contrat != 0 && (m_passes >= 3 || m_surcoinche == true))
			return (1);
		return (ret);
	}
	
	private boolean checkManche(Player assoc, Player ennemie1, Player ennemie2)
	{
		int score = m_player.getScore();
		if (m_contrat == 500)
			assoc = null;
		else
			score += assoc.getScore();
		if (score < 82)
			return (false);
		score += m_player.getBellote();
		if (assoc != null)
			score += assoc.getBellote();
		if (score < m_contrat)
			return (false);
		score += m_contrat;
		if (m_coinche == true)
			score *= 2;
		else if (m_surcoinche == true)
			score *= 4;
		m_team.addScore(score);
		score = ennemie1.getBellote() + ennemie2.getBellote();
		if (m_coinche == false)
			score += ennemie1.getScore() + ennemie2.getScore();
		ennemie1.getTeam().addScore(score);
		return (true);
	}
	
	public boolean endManche(ArrayList<Player> lstPlayer)
	{
		int idx = lstPlayer.indexOf(m_player);
		Player assoc = lstPlayer.get((idx + 2) % 4);
		Player ennemie1 = lstPlayer.get((idx + 1) % 4);
		Player ennemie2 = lstPlayer.get((idx + 3) % 4);		
		
		if (checkManche(assoc, ennemie1, ennemie2) == false)
		{
			m_team.addScore(m_player.getBellote());
			m_team.addScore(assoc.getBellote());
			Team t2 = ennemie1.getTeam();
			int score = 162 + m_contrat + ennemie1.getBellote() + ennemie2.getBellote();
			if (m_coinche == true)
				score *= 2;
			else if (m_surcoinche == true)
				score *= 4;
			t2.addScore(score);
			return false;
		}
		return true;
	}
	
}
