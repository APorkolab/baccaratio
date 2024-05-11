package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.model.Deck;
import com.prokey.baccaratio.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BaccaratService {
    private Deck deck;
    private Player player;
    private String lastResult = "";
    private String betType = "";
    private int betAmount = 0;
    private List<Card> playerCards = new ArrayList<>();
    private List<Card> bankerCards = new ArrayList<>();


    @Autowired
    public BaccaratService(Deck deck) {
        this.deck = deck;
        this.player = new Player("Babiagorai Riparievich Metell", 1000);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean placeBet(String type, int amount) {
        if (amount <= 0 || !isValidType(type)) {
            return false;
        }
        if (this.player.getChips() < amount) {
            return false;
        }
        this.betType = type;
        this.betAmount = amount;
        return true;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public String playRound() {

        if (this.player.getChips() < this.betAmount) {
            return "You do not have enough chips to place a bet. The game is over";
        }

        playerCards = new ArrayList<>();
        bankerCards = new ArrayList<>();

        // Draw the cards
        playerCards.add(deck.draw());
        playerCards.add(deck.draw());
        bankerCards.add(deck.draw());
        bankerCards.add(deck.draw());

        // Reshuffle if less than 6 cards left
        if (deck.getCards().size() < 6) {
            deck.reshuffle();
        }

        int playerTotal = calculateTotal(playerCards.get(0), playerCards.get(1));
        int bankerTotal = calculateTotal(bankerCards.get(0), bankerCards.get(1));

        // A játék logikája
        if (!(playerTotal == 8 || playerTotal == 9 || bankerTotal == 8 || bankerTotal == 9)) {
            Card playerThirdCard = null;
            if (playerTotal <= 5) {
                playerThirdCard = deck.draw();
                playerCards.add(playerThirdCard);
                playerTotal = calculateTotalWithCard(playerTotal, playerThirdCard);
            }

            Card bankerThirdCard = null;
            if (shouldBankerDraw(bankerTotal, playerTotal, playerThirdCard)) {
                bankerThirdCard = deck.draw();
                bankerCards.add(bankerThirdCard);
                bankerTotal = calculateTotalWithCard(bankerTotal, bankerThirdCard);
            }
        }

        lastResult = determineOutcome(playerTotal, bankerTotal);
        updateChipsBasedOnResult();

        // Check if the player has any chips left
        if (this.player.getChips() <= 0) {
            return "You are out of chips. You lose.";
        }


        return lastResult;
    }

    private void updateChipsBasedOnResult() {
        // Get the first two cards from the lists
        Card playerCard1 = playerCards.size() > 0 ? playerCards.get(0) : null;
        Card playerCard2 = playerCards.size() > 1 ? playerCards.get(1) : null;
        Card bankerCard1 = bankerCards.size() > 0 ? bankerCards.get(0) : null;
        Card bankerCard2 = bankerCards.size() > 1 ? bankerCards.get(1) : null;

        // Check for possible third cards
        Card playerThirdCard = playerCards.size() > 2 ? playerCards.get(2) : null;
        Card bankerThirdCard = bankerCards.size() > 2 ? bankerCards.get(2) : null;

        int payout = 0;
        boolean isWin = false;

        // Betting types and logic of winners
        switch (this.betType) {
            case "player":
                if (this.lastResult.startsWith("Player won")) {
                    payout = this.betAmount * 2;
                    isWin = true;
                }
                break;
            case "banker":
                if (this.lastResult.startsWith("Banker won")) {
                    payout = (int) (this.betAmount * 1.95);
                    isWin = true;
                }
                break;
            case "tie":
                if (this.lastResult.startsWith("Draw")) {
                    payout = this.betAmount * 8;
                    isWin = true;
                }
                break;
            case "perfectPairOne":
                if ((playerCard1 != null && playerCard2 != null && isPerfectPair(playerCard1, playerCard2)) ||
                        (bankerCard1 != null && bankerCard2 != null && isPerfectPair(bankerCard1, bankerCard2))) {
                    payout = this.betAmount * 25;
                    isWin = true;
                }
                break;
            case "playerPair":
                if (playerCard1 != null && playerCard2 != null && isPair(playerCard1, playerCard2)) {
                    payout = this.betAmount * 11;
                    isWin = true;
                }
                break;
            case "bankerPair":
                if (bankerCard1 != null && bankerCard2 != null && isPair(bankerCard1, bankerCard2)) {
                    payout = this.betAmount * 11;
                    isWin = true;
                }
                break;
            case "eitherPair":
                if ((playerCard1 != null && playerCard2 != null && isPair(playerCard1, playerCard2)) ||
                        (bankerCard1 != null && bankerCard2 != null && isPair(bankerCard1, bankerCard2))) {
                    payout = this.betAmount * 5;
                    isWin = true;
                }
                break;
            default:
                break;
        }

        if (isWin) {
            this.player.win(payout);
        } else {
            this.player.lose(this.betAmount);
        }

        // Reset variables for the next round
        this.betType = "";
        this.betAmount = 0;
    }

    private boolean isPerfectPair(Card card1, Card card2) {
        return card1.getSuit().equals(card2.getSuit()) && card1.getValue().equals(card2.getValue());
    }

    private boolean isPair(Card card1, Card card2) {
        return card1.getValue().equals(card2.getValue());
    }

    private boolean shouldBankerDraw(int bankerTotal, int playerTotal, Card playerThirdCard) {
        if (bankerTotal >= 7) {
            // Banker's total is 7 or higher: no further card draws.
            return false;
        } else if (bankerTotal >= 3 && bankerTotal <= 6) {
            // Special rules apply if the banker total is between 3 and 6.
            if (playerThirdCard == null) {
                // If the player has not drawn a third card, the banker draws,
                // if its total value is 5 or less.
                return bankerTotal <= 5;
            } else {
                // The player has drawn a third card, so the decision depends on the value of the card.
                int playerThirdCardValue = playerThirdCard.getPoints();
                switch (bankerTotal) {
                    case 3:
                        // Banker draws unless the player's third card is 8.
                        return playerThirdCardValue != 8;
                    case 4:
                        // Banker draws if player's third card is between 2-7.
                        return playerThirdCardValue >= 2 && playerThirdCardValue <= 7;
                    case 5:
                        // Banker draws if player's third card is between 4-7.
                        return playerThirdCardValue >= 4 && playerThirdCardValue <= 7;
                    case 6:
                        // Banker draws if the player's third card is either 6 or 7.
                        return playerThirdCardValue == 6 || playerThirdCardValue == 7;
                    default:
                        // In all other cases, the banker does not draw.
                        return false;
                }
            }
        } else {
        // If the banker's total score is 0, 1, or 2, always draw a card.
        return true;
    }
}

    private int calculateTotal(Card... cards) {
        int total = 0;
        for (Card card : cards) {
            total += card.getPoints();
        }
        return total % 10;
    }

    private int calculateTotalWithCard(int currentTotal, Card card) {
        return (currentTotal + card.getPoints()) % 10;
    }

    private String naturalWinResult(int playerTotal, int bankerTotal) {
        if (playerTotal == bankerTotal) {
            return "Draw! Both sides score: " + playerTotal;
        } else if (playerTotal == 8 || playerTotal == 9) {
            return "Player wins! Score: " + playerTotal + " vs. Banker's score: " + bankerTotal;
        } else {
            return "Banker won! Score: " + bankerTotal + " vs. player score: " + playerTotal;
        }
    }

    private String determineOutcome(int playerTotal, int bankerTotal) {
        if (playerTotal > bankerTotal) {
            return "Player won! Score: " + playerTotal + " vs. Banker's score: " + bankerTotal;
        } else if (bankerTotal > playerTotal) {
            return "Banker won! Score: " + bankerTotal + " vs. Player's score: " + playerTotal;
        } else {
            return "Tie! Both sides score: " + playerTotal;
        }
    }

    public int getChips() {
        return player.getChips();
    }

    public String getLastResult() {
        return lastResult;
    }

    public String getBetType() {
        return betType;
    }

    public List<Card> getPlayerCards() {
        return playerCards;
    }

    public List<Card> getBankerCards() {
        return bankerCards;
    }

    public boolean updateChips(int amount) {
        if (player != null) {
            player.setChips(player.getChips() + amount);
            return true;
        }
        return false;
    }

    public boolean isValidType(String type){
        return Deck.isValidBetType(type);
    }
}

