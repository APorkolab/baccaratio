package com.prokey.baccaratio.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Component
public class Deck {
    private List<Card> cards = new ArrayList<>();

    public Deck() {
        reshuffle();
    }

    public void reshuffle() {
        cards.clear(); // Törli a korábbi kártyákat
        initializeDeck(); // Újra inicializálja a paklit
    }

    private void initializeDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        int[] points = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0}; // Baccarat szabályok szerint

        for (String suit : suits) {
            for (int i = 0; i < values.length; i++) {
                cards.add(new Card(suit, values[i], points[i]));
            }
        }
        Collections.shuffle(cards);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            reshuffle(); // Ha a pakli üres, újrakeveri azt
        }
        return cards.remove(cards.size() - 1);
    }

    public int size() {
        return cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}