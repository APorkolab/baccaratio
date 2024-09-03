package com.prokey.baccaratio.model;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents a deck of cards for the Baccarat game.
 */
@Component
public class Deck {
    private List<Card> cards = new ArrayList<>();

    public Deck() {
        reshuffle();
    }

    /**
     * Reshuffles the deck by clearing existing cards and reinitializing the deck.
     */
    public void reshuffle() {
        cards.clear(); // Clears previous cards
        initializeDeck(); // Reinitialize the deck
    }

    /**
     * Initializes the deck with a standard set of Baccarat cards.
     */
    private void initializeDeck() {
        String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };
        String[] values = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
        int[] points = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0 }; // Points according to Baccarat rules

        for (String suit : suits) {
            for (int i = 0; i < values.length; i++) {
                cards.add(new Card(suit, values[i], points[i]));
            }
        }
        Collections.shuffle(cards); // Shuffles the deck
    }

    private static final Set<String> VALID_BET_TYPES = Set.of(
            "player",
            "banker",
            "tie",
            "perfectPairOne",
            "playerPair",
            "eitherPair",
            "bankerPair");

    /**
     * Draws a card from the deck. If the deck is empty, it reshuffles before
     * drawing.
     * 
     * @return the drawn card
     */
    public synchronized Card draw() {
        if (cards.isEmpty()) {
            reshuffle(); // Reshuffle if the deck is empty
        }
        return cards.remove(cards.size() - 1); // Removes and returns the last card
    }

    public int size() {
        return cards.size();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards); // Returns a copy of the list to prevent external modifications
    }

    public void setCards(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            throw new IllegalArgumentException("Cannot set an empty or null list as the deck.");
        }
        this.cards = new ArrayList<>(cards); // Ensures the deck is a copy of the provided list, not the list itself
    }

    /**
     * Checks if a given bet type is valid.
     * 
     * @param type The bet type to check.
     * @return true if the type is a valid bet type, false otherwise.
     */
    public static boolean isValidBetType(String type) {
        return VALID_BET_TYPES.contains(type);
    }
}
