package com.prokey.baccaratio.model;

/**
 * Represents a single playing card used in Baccarat, with a suit, value, and point score.
 */
public class Card {
    private final String suit; // e.g., "Hearts", "Diamonds", "Clubs", "Spades"
    private final String value; // e.g., "A", "2", "3", ..., "K"
    private final int points; // Points according to Baccarat rules

    /**
     * Constructs a new Card with the specified suit, value, and points.
     * @param suit the card's suit
     * @param value the card's value
     * @param points the card's point value in Baccarat
     */
    public Card(String suit, String value, int points) {
        if (suit == null || suit.isEmpty() || value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Suit and value must not be empty or null.");
        }
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative.");
        }
        this.suit = suit;
        this.value = value;
        this.points = points;
    }

    public String getSuit() {
        return suit;
    }

    public String getValue() {
        return value;
    }

    public int getPoints() {
        return points;
    }
}
