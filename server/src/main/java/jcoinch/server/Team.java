package jcoinch.server;

public class Team {
	private int 	m_totalScore;
	//private Annonce m_annonce;

	public Team()
	{
		m_totalScore = 0;
	}
	
	public int getTotalScore()
	{
		return m_totalScore;
	}
	
	public void addScore(int score)
	{
		m_totalScore += score;
	}
	
}
