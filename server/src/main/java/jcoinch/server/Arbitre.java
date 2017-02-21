package jcoinch.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jcoinch.utils.Card;
import jcoinch.utils.Card.Color;

public class Arbitre {
	private Map<Integer, int[]> rules;
public Arbitre()
{
	Map<Integer, int[]> hm = new HashMap<Integer, int[]>();
    int[] As = {11, 11, 6, 19};
    int[] Seven = {0, 0, 0, 0};
    int[] Height = {0, 0, 0, 0};
    int[] Nine = {0, 14, 9, 0};
    int[] Ten = {10, 10, 5, 10};
    int[] Jack = {2, 20, 14, 2};
    int[] Queen = {3, 3, 1, 3};
    int[] King = {4, 4, 3, 4};
    hm.put(0, Seven);
    hm.put(1, Height);
    hm.put(2, Nine);
    hm.put(3, Ten);
    hm.put(4, Jack);
    hm.put(5, Queen);
    hm.put(6, King);
    hm.put(7, As);
    this.rules = hm;
}

private boolean compareCard(Card first, Card second, int mode)
{
	if (this.rules.get(first.value.getValue())[mode] > this.rules.get(second.value.getValue())[mode] ||
			(this.rules.get(first.value.getValue())[mode] == this.rules.get(second.value.getValue())[mode] && first.value.getValue() > second.value.getValue()))
		return true;
	return false;
}

private int getHCard(ArrayList<Card> list, int mode, Card.Color color)
{
	int index = -1;
	for (int i = 0; i < list.size(); i++){
		if ((index == -1 && color == list.get(i).color) || (index != -1 && compareCard(list.get(i), list.get(index), mode) == true && color == list.get(i).color))
			index = i;
	}
	return index;
}

private boolean have_color(Card.Color color, ArrayList<Card> list)
{
	for (int i = 0; i < list.size(); i++){
		if (list.get(i).color == color)
			return true;
	}
	return false;
}

private boolean check_color(Card.Color trump, ArrayList<Card> hand, Card card, Card.Color first, ArrayList<Card> trick)
{
	if (first == trump)
	{
		Card hCard = trick.get(getHCard(trick, 1, first));
		if(card.color != first || compareCard(card, hCard, 1) == false)
			if (getHCard(hand, 1, first) != -1 && compareCard(hand.get(getHCard(hand, 1, first)), hCard, 1) == true)
				return false;
			else if (card.color != first && have_color(first, hand) == true)
				return false;
	}
	else
	{
		if (card.color != first)
			if (have_color(first, hand) == true)
				return false;
			else if (card.color != trump && have_color(trump, hand) == true)
				return false;
			else if (card.color == trump)
			{
				if (getHCard(hand, 1, trump) != -1 && getHCard(trick, 1, trump) != -1 && compareCard(card, trick.get(getHCard(trick, 1, trump)), 1) == false && have_color(trump, hand) == true && compareCard(hand.get(getHCard(hand, 1, trump)), trick.get(getHCard(trick, 1, trump)), 1) == true)
					return false;
			}
	}
	return true;
}

private boolean all_trump(ArrayList<Card> hand, Card card, Card.Color first, ArrayList<Card> trick)
{
	if (card.color != first && have_color(first, hand) == true)
		return false;
	else if (getHCard(hand, 2, first) != -1 && getHCard(trick, 2, first) != -1 && card.color == first && compareCard(card, trick.get(getHCard(trick, 2, first)), 2) == false && have_color(first, hand) == true && compareCard(hand.get(getHCard(hand, 2, first)), trick.get(getHCard(trick, 2, first)), 2) == true)
		return false;
	return true;
}

private boolean no_trump(ArrayList<Card> hand, Card card, Card.Color first, ArrayList<Card> trick)
{
	if (card.color != first && have_color(first, hand) == true)
		return false;
	return true;
}

protected int win_color(Card.Color trump, Card.Color first, ArrayList<Card> trick)
{
if (have_color(trump, trick) == true)
	return (getHCard(trick, 1, trump));
else
	return (getHCard(trick, 0, first));
}

protected int win_no_trump(Card.Color first, ArrayList<Card> trick)
{
	return (getHCard(trick, 3, first));
}

protected int win_all_trump(Card.Color first, ArrayList<Card> trick)
{
	return (getHCard(trick, 2, first));
}

public boolean check_coup(Mode mode, ArrayList<Card> hand, Card card, ArrayList<Card> trick)
{
	Card.Color first = trick.get(0).color;
	switch (mode)
	{
	case NOTRUMP:
		if (no_trump(hand, card, first, trick) == false)
			return false;
		break;
	case ALLTRUMP:
		if (all_trump(hand, card, first, trick) == false)
			return false;
		break;
	case SPADE:
		if (check_color(Card.Color.SPADE, hand, card, first, trick) == false)
			return false;
		break;
	case CLUB:
		if (check_color(Card.Color.CLUB, hand, card, first, trick) == false)
			return false;
		break;
	case HEART:
		if (check_color(Card.Color.HEART, hand, card, first, trick) == false)
			return false;
		break;
	case DIAMOND:
		if (check_color(Card.Color.DIAMOND, hand, card, first, trick) == false)
			return false;
		break;
	}
	return true;
}

public Integer win_trick(Mode mode, ArrayList<Card> trick)
{
	Card.Color first = trick.get(0).color;
	switch (mode)
	{
	case NOTRUMP:
		return (win_no_trump(first, trick));
	case ALLTRUMP:
		return (win_all_trump(first, trick));
	case SPADE:
		return (win_color(Card.Color.SPADE, first, trick));
	case CLUB:
		return (win_color(Card.Color.CLUB, first, trick));
	case HEART:
		return (win_color(Card.Color.HEART, first, trick));
	case DIAMOND:
		return (win_color(Card.Color.DIAMOND, first, trick));
	}
	return -1;
}

private Integer pt_color(Color trump, Card card)
{
	if (card.color == trump)
		return (this.rules.get(card.value.getValue())[1]);
	else
		return (this.rules.get(card.value.getValue())[0]);
}

public Integer trick_points(Mode mode, ArrayList<Card> trick)
{
	int result = 0;
	for (int i = 0; i < trick.size(); i++){
		switch (mode)
		{
		case NOTRUMP:
			result += this.rules.get(trick.get(i).value.getValue())[3];
			break;
		case ALLTRUMP:
			result += this.rules.get(trick.get(i).value.getValue())[2];
			break;
		case SPADE:
			result += pt_color(Color.SPADE, trick.get(i));
			break;
		case CLUB:
			result += pt_color(Color.CLUB, trick.get(i));
			break;
		case HEART:
			result += pt_color(Color.HEART, trick.get(i));
			break;
		case DIAMOND:
			result += pt_color(Color.DIAMOND, trick.get(i));
			break;
		}
	}
	return result;
}

}
