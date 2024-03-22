package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.model.Deck;
import com.prokey.baccaratio.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaccaratService {
    private Deck deck;
    private Player player;
    private String lastResult = "";
    private String betType = "";
    private int betAmount = 0;

    @Autowired
    public BaccaratService(Deck deck) {
        this.deck = deck;
        this.player = new Player("Default Player", 100);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean placeBet(String type, int amount) {
        if (amount <= 0 || (!type.equals("player") && !type.equals("banker") && !type.equals("tie")
                && !type.equals("perfectPairOne") && !type.equals("pPair")
                && !type.equals("eitherPair") && !type.equals("bPair"))) {
            return false; // Érvénytelen tét összeg vagy típus
        }
        if (this.player.getChips() < amount) {
            return false; // Nincs elég zseton a fogadáshoz
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
            return "Nincs elég zsetonod a fogadáshoz. A játék véget ért.";
        }

        Card playerCard1 = deck.draw();
        Card playerCard2 = deck.draw();
        Card bankerCard1 = deck.draw();
        Card bankerCard2 = deck.draw();

        if (deck.getCards().size() < 6) {
            deck.reshuffle();
        }

        int playerTotal = calculateTotal(playerCard1, playerCard2);
        int bankerTotal = calculateTotal(bankerCard1, bankerCard2);

        if (playerTotal == 8 || playerTotal == 9 || bankerTotal == 8 || bankerTotal == 9) {
            lastResult = naturalWinResult(playerTotal, bankerTotal);
        } else {
            Card playerThirdCard = null;
            if (playerTotal <= 5) {
                playerThirdCard = deck.draw();
                playerTotal = calculateTotalWithCard(playerTotal, playerThirdCard);
            }

            Card bankerThirdCard = null;
            if (shouldBankerDraw(bankerTotal, playerTotal, playerThirdCard)) {
                bankerThirdCard = deck.draw();
                bankerTotal = calculateTotalWithCard(bankerTotal, bankerThirdCard);
            }

            lastResult = determineOutcome(playerTotal, bankerTotal);
        }
        updateChipsBasedOnResult(playerCard1, playerCard2, bankerCard1, bankerCard2);

        // Ellenőrizzük, hogy a játékosnak van-e még zsetonja
        if (this.player.getChips() <= 0) {
            return "Elfogytak a zsetonjaid. Vesztettél.";
        }

        return lastResult;
    }

    private void updateChipsBasedOnResult(Card playerCard1, Card playerCard2, Card bankerCard1, Card bankerCard2) {
        int payout = 0;
        boolean isWin = false;

        switch (this.betType) {
            case "player":
                if (this.lastResult.startsWith("Játékos nyert")) {
                    payout = this.betAmount * 2;
                    isWin = true;
                }
                break;
            case "banker":
                if (this.lastResult.startsWith("Bankár nyert")) {
                    payout = (int) (this.betAmount * 1.95);
                    isWin = true;
                }
                break;
            case "tie":
                if (this.lastResult.startsWith("Döntetlen")) {
                    payout = this.betAmount * 8;
                    isWin = true;
                }
                break;
            case "perfectPairOne":
                if (isPerfectPair(playerCard1, playerCard2) || isPerfectPair(bankerCard1, bankerCard2)) {
                    payout = this.betAmount * 25;
                    isWin = true;
                }
                break;
            case "playerPair":
                if (isPair(playerCard1, playerCard2)) {
                    payout = this.betAmount * 11;
                    isWin = true;
                }
                break;
            case "bankerPair":
                if (isPair(bankerCard1, bankerCard2)) {
                    payout = this.betAmount * 11;
                    isWin = true;
                }
                break;
            case "eitherPair":
                if (isPair(playerCard1, playerCard2) || isPair(bankerCard1, bankerCard2)) {
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
        // Logic for deciding if the banker should draw a third card
        // This method remains unchanged
        return false; // Simplified for illustration
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
            return "Döntetlen! Mindkét fél pontszáma: " + playerTotal;
        } else if (playerTotal == 8 || playerTotal == 9) {
            return "Játékos nyert! Pontszám: " + playerTotal + " vs. Bankár pontszáma: " + bankerTotal;
        } else {
            return "Bankár nyert! Pontszám: " + bankerTotal + " vs. Játékos pontszáma: " + playerTotal;
        }
    }

    private String determineOutcome(int playerTotal, int bankerTotal) {
        if (playerTotal > bankerTotal) {
            return "Játékos nyert! Pontszám: " + playerTotal + " vs. Bankár pontszáma: " + bankerTotal;
        } else if (bankerTotal > playerTotal) {
            return "Bankár nyert! Pontszám: " + bankerTotal + " vs. Játékos pontszáma: " + playerTotal;
        } else {
            return "Döntetlen! Mindkét fél pontszáma: " + playerTotal;
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
}

