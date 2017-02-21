package jcoinch.utils;

public class Card {
	public final Color color;
	public final Value value;
	public enum Color {
		SPADE(0),
		CLUB(1),
		HEART(2),
		DIAMOND(3);
			
			private final int value;
			private Color(int value){
				this.value = value;
			}
			    public int getValue() {
			        return value;
			    }

		};		
	public enum Value {
			SEVEN(0),
			EIGHT(1),
			NINE(2),
			TEN(3),
			JACK(4),
			QUEEN(5),
			KING(6),
			AS(7);
			
			private final int value;
			private Value(int value){
				this.value = value;
			}
			    public int getValue() {
			        return value;
			    }
		}
	public Card(Color color, Value value)
	{
		this.color = color;
		this.value = value;
	}
	public Card(int color, int value){
		this.color = Color.values()[color];
		this.value = Value.values()[value];
	}
}
