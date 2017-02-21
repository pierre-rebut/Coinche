package jcoinch.server;

import java.util.ArrayList;

import io.netty.channel.Channel;
import jcoinch.utils.Card;
import jcoinch.utils.Transmition;

public class GameGroup
{
	private 	  Mode				m_trump;
	private final Contrat			m_contrat;
	private final Deck				m_deck;
	private final Board				m_board;
	private final ArrayList<Player>	m_lstPlayer;
	private 	  int 				m_currentPlayer;
	private		  int				m_lastWinner;
	private 	  int				m_dealer;
	private 	  Type				m_event;
	
	enum Type {
		ENCHERE,
		ATOUT,
		ANNONCE,
		MANCHE;
	};
	
	public GameGroup(Channel ch1, Channel ch2, Channel ch3, Channel ch4)
	{
		m_contrat = new Contrat();
		m_deck = new Deck();
		m_board = new Board();
		m_lstPlayer = new ArrayList<Player>();
		Team team1 = new Team();
		Team team2 = new Team();
		m_lstPlayer.add(new Player("Player 1", 0, ch1, team1));
		m_lstPlayer.add(new Player("Player 2", 1, ch2, team2));
		m_lstPlayer.add(new Player("Player 3", 2, ch3, team1));
		m_lstPlayer.add(new Player("Player 4", 3, ch4, team2));
		m_currentPlayer = 0;
		m_dealer = 0;
		m_lastWinner = 0;
		m_event = Type.ENCHERE;
		m_deck.initDeck();
		for (Player pl : m_lstPlayer)
		{
			pl.send(new Transmition("INFO", "-----   Game Start   -----"));
			pl.setDeck(m_deck.createDeck());
		}
		m_lstPlayer.get(m_currentPlayer).send(new Transmition("INFO", "-- Choose contrat --"));
	}
	
	private Player getPlayer(Channel ch)
	{
		for (Player pl : m_lstPlayer)
		{
			if (pl.getChannel() == ch)
				return pl;
		}
		return null;
	}
	
	private void changePlayer()
	{
		m_currentPlayer = (m_currentPlayer + 1) % 4;
		m_lstPlayer.get(m_currentPlayer).send(new Transmition("INFO", "-- Your turn --"));
	}
	
	private void sendAll(Transmition msg)
	{
		for (Player pl : m_lstPlayer)
			pl.send(msg);
	}
	
	public void execCommand(Channel ch, Transmition msg)
	{
		Player pl = getPlayer(ch);
		String cmd = "cmd_" + msg.m_type.toLowerCase();
		try
		{
			this.getClass().getMethod(cmd, Player.class, Transmition.class).invoke(this, pl, msg);
		}
		catch (Exception e)
		{
			pl.send(new Transmition("ERROR", "Invalid command"));
		}
	}
	
	public void cmd_exit(Player player, Transmition msg)
	{
		for (Player pl : m_lstPlayer)
			pl.exit();
	}
	
	public void cmd_contrat(Player pl, Transmition msg)
	{
		if (m_event != Type.ENCHERE)
			pl.send(new Transmition("ERROR", "Contrat not required"));
		else if (m_lstPlayer.get(m_currentPlayer) != pl)
			pl.send(new Transmition("INFO", "Please wait until your turn"));
		else
		{
			int res = m_contrat.checkContrat(pl, msg.m_cmd);
			switch (res)
			{
			case -1:
				sendAll(new Transmition("INFO", pl.getName() + " passe"));
				break;
			case -2:
				sendAll(new Transmition("INFO", pl.getName() + " coinche"));
				break;
			case -3:
				sendAll(new Transmition("INFO", pl.getName() + " surcoinche"));
				break;
			case -4:
				sendAll(new Transmition("INFO", pl.getName() + " contrat to " + m_contrat.getContrat()));
				break;
			case 1:
				sendAll(new Transmition("INFO", "Partie start : contrat -> " + m_contrat.getContrat()));
				m_event = Type.ATOUT;
				m_currentPlayer = m_contrat.getPlayerContrat().getId();
				m_lstPlayer.get(m_currentPlayer).send(new Transmition("INFO", "Choose trump"));
				break;
			case 2:
				sendAll(new Transmition("INFO", "Nobody contrated : Partie restart"));
				m_contrat.reset();
				m_deck.initDeck();
				m_currentPlayer = m_dealer;
				for (Player tmp : m_lstPlayer)
					tmp.setDeck(m_deck.createDeck());
				break;
			};
			if (res < 0)
				changePlayer();
		}
	}
	
	public void cmd_trump(Player pl, Transmition msg)
	{
		if (m_event == Type.MANCHE && msg.m_cmd == null)
			pl.send(new Transmition("INFO", "Trump : " + m_trump.toString()));
		else if (m_event != Type.ATOUT)
			pl.send(new Transmition("ERROR", "ATOUT not required"));
		else if (m_lstPlayer.get(m_currentPlayer) != pl)
			pl.send(new Transmition("INFO", "Please wait until your turn"));
		else if (msg.m_cmd != null)
		{
			for (Mode tmp : Mode.values())
				if (tmp.toString().equalsIgnoreCase(msg.m_cmd))
				{
					m_trump = tmp;
					sendAll(new Transmition("INFO", pl.getName() + " choose trump : " + m_trump.toString()));
					m_event = Type.MANCHE;
					m_currentPlayer = m_dealer;
					m_lstPlayer.get(m_currentPlayer).send(new Transmition("INFO", "---  Start  ---"));
					for (Player joueur : m_lstPlayer)
						joueur.checkBelote(m_trump);
					break;
				}
		}
		else
			pl.send(new Transmition("ERROR", "Trump error"));
	}
	
	private void checkIfEndGame()
	{
		int scoreTeam1 = m_lstPlayer.get(0).getTeam().getTotalScore();
		int scoreTeam2 = m_lstPlayer.get(1).getTeam().getTotalScore();
		
		if (scoreTeam1 <= 700 && scoreTeam2 <= 700)
			return ;
		if (scoreTeam1 >= scoreTeam2 && scoreTeam1 > 700)
			sendAll(new Transmition("WINNER", "Team 1 is winner !!!!"));
		else
			sendAll(new Transmition("WINNER", "Team 2 is winner !!!!"));
		for (Player pl : m_lstPlayer)
			pl.exit();
	}
	
	private void putCard(Player pl, int num)
	{
		Card card = pl.getCard(num);
		if (m_board.putCard(pl, card, m_trump) >= 0)
		{
			pl.removeCard(num);
			ArrayList<Card> tmp2 = new ArrayList<Card>(); tmp2.add(card);
			sendAll(new Transmition("PUT", pl.getName(), tmp2 ));
			int res = m_board.checkIfWinner(card, m_trump);
			if (res != -1)
			{
				m_currentPlayer = (res + m_lastWinner) % 4;
				m_lastWinner = m_currentPlayer;
				int score = m_board.getScore(m_trump);
				sendAll(new Transmition("INFO", "End plis: winner -> " + m_lstPlayer.get(m_currentPlayer).getName() + " = " + score));
				m_lstPlayer.get(m_currentPlayer).addScore(score);
				if (m_board.checkIfEndManche() == 1)
				{
					m_board.reset();
					m_lstPlayer.get(m_currentPlayer).addScore(10);
					sendAll(new Transmition("INFO", "-----  End manche  -----"));
					if (m_contrat.endManche(m_lstPlayer) == true)
						sendAll(new Transmition("INFO", m_contrat.getPlayerContrat().getName() + " win is contrat -> " + m_contrat.getPlayerContrat().getTeam().getTotalScore()));
					else
						sendAll(new Transmition("INFO", m_contrat.getPlayerContrat().getName() + " failed is contrat (" + m_contrat.getContrat() + ")"));
					checkIfEndGame();
					m_dealer = (m_dealer + 1) % 4;
					m_currentPlayer = m_dealer;
					m_lastWinner = m_dealer;
					m_event = Type.ENCHERE;
					m_contrat.reset();
					m_deck.initDeck();
					for (Player tmp : m_lstPlayer)
					{
						tmp.setDeck(m_deck.createDeck());
						tmp.reset();
					}
					m_lstPlayer.get(m_currentPlayer).send(new Transmition("INFO", "-- Choose contrat --"));
				}
				else
					m_lstPlayer.get(m_currentPlayer).send(new Transmition("INFO", "---  Your turn  ---"));
			}
			else
				changePlayer();
		}
	}
	
	public void cmd_put(Player pl, Transmition msg)
	{
		if (m_event != Type.MANCHE)
			pl.send(new Transmition("ERROR", "Manche not started"));
		else if (m_lstPlayer.get(m_currentPlayer) != pl)
			pl.send(new Transmition("INFO", "Please wait until your turn"));
		else
		{
			int num = Integer.parseInt(msg.m_cmd);
			if (num < 0 || num >= pl.getNbCard())
				pl.send(new Transmition("ERROR", "put: num card must be between 0 and " + pl.getNbCard()));
			else
			{
				putCard(pl, num);
			}
		}
	}
	
	public void cmd_score(Player pl, Transmition msg)
	{
		pl.send(new Transmition("INFO", "My score : " + pl.getScore() + "\nScore Team: " + pl.getTeam().getTotalScore()));
	}
	
	public void cmd_help(Player pl, Transmition msg)
	{
		if (msg.m_cmd == null)
			pl.send(new Transmition("INFO", "HELP:\n-contrat <val>\t-trump <color>\n-put <num card>\t-help [<cmd>]\n-show [BOARD | HAND]\t-msg <message>"));
		else
		{
			if (msg.m_cmd.equalsIgnoreCase("contrat"))
				pl.send(new Transmition("INFO", "contrat -> create a contrat (val must be a mul of 10 and between 80 and 160)"));
			else if (msg.m_cmd.equalsIgnoreCase("trump"))
				pl.send(new Transmition("INFO", "trump -> choose trump (val must be a color: DIAMOND, SPADE, NOTRUMP, ALLTRUMP, CLUB, HEART)"));
			else if (msg.m_cmd.equalsIgnoreCase("put"))
				pl.send(new Transmition("INFO", "put -> put card on board (val must be between 0 and max num card)"));
			else if (msg.m_cmd.equalsIgnoreCase("help"))
				pl.send(new Transmition("INFO", "help -> detail command"));
			else if (msg.m_cmd.equalsIgnoreCase("show"))
				pl.send(new Transmition("INFO", "show -> show hand or board"));
			else if (msg.m_cmd.equalsIgnoreCase("msg"))
				pl.send(new Transmition("INFO", "msg -> send message to all"));
			else
				pl.send(new Transmition("INFO", "help: invalid arg"));
		}
	}
	
	public void cmd_msg(Player pl, Transmition msg)
	{
		for (Player tmp : m_lstPlayer)
		{
			if (tmp == pl)
				tmp.send(new Transmition("MSG", "you : " + msg.m_cmd));
			else
				tmp.send(new Transmition("MSG", pl.getName() + " : " + msg.m_cmd));
		}
	}
	
	public void cmd_name(Player pl, Transmition msg)
	{
		if (msg.m_cmd == null || msg.m_cmd.trim().equals(""))
			pl.send(new Transmition("ERROR", "name: invalid arg"));
		else
		{
			boolean res = false;
			for (Player tmp : m_lstPlayer)
			{
				if (tmp.getName().equals(msg.m_cmd))
					res = true;
			}
			if (res == true)
				pl.send(new Transmition("ERROR", "name already use"));
			else
			{
				sendAll(new Transmition("INFO", pl.getName() + " change name to " + msg.m_cmd));
				pl.setName(msg.m_cmd);
			}
		}
	}
	
	public void cmd_show(Player pl, Transmition msg)
	{
		if (msg.m_cmd == null)
			pl.send(new Transmition("ERROR", "no arg"));
		else if (msg.m_cmd.equalsIgnoreCase("HAND"))
			pl.showHand();
		else if (msg.m_cmd.equalsIgnoreCase("BOARD"))
		{
			if (m_event != Type.MANCHE)
				pl.send(new Transmition("ERROR", "Manche not started"));
			else
				m_board.showBoard(pl);
		}
		else
			pl.send(new Transmition("ERROR", "Invalid arg"));
	}
}
