package com.prokey.baccaratio.controller.dto;

import com.prokey.baccaratio.model.Card;
import java.util.List;

public class GameResponse {
    private String result;
    private List<Card> playerCards;
    private List<Card> bankerCards;
    private int playerChips;
    private String message;

    // Constructors
    public GameResponse() {}

    public GameResponse(String result, List<Card> playerCards, List<Card> bankerCards, int playerChips, String message) {
        this.result = result;
        this.playerCards = playerCards;
        this.bankerCards = bankerCards;
        this.playerChips = playerChips;
        this.message = message;
    }

    // Getters and Setters
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Card> getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(List<Card> playerCards) {
        this.playerCards = playerCards;
    }

    public List<Card> getBankerCards() {
        return bankerCards;
    }

    public void setBankerCards(List<Card> bankerCards) {
        this.bankerCards = bankerCards;
    }

    public int getPlayerChips() {
        return playerChips;
    }

    public void setPlayerChips(int playerChips) {
        this.playerChips = playerChips;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
