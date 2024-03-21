package com.prokey.baccaratio.model;

public class Card {
    private String suit; // például: "Hearts", "Diamonds", "Clubs", "Spades"
    private String value; // például: "A", "2", "3", ..., "K"
    private int points; // Baccarat pontértékek

    public Card(String suit, String value, int points) {
        this.suit = suit;
        this.value = value;
        this.points = points;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
