package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.model.Deck;
import com.prokey.baccaratio.model.Player;
import org.springframework.stereotype.Service;

@Service
public class BaccaratService {
    private Deck deck;
    private Player player;
    private String lastResult = "";
    private String betType = "";
    private int betAmount = 0;

    public BaccaratService() {
        this.deck = new Deck();
        this.player = new Player("Default Player", 100); // Kezdeti zsetonok száma
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean placeBet(String type, int amount) {
        if (player.getChips() >= amount) {
            this.betType = type;
            this.betAmount = amount;
            // Itt további logika lehet, ha szükséges
            return true;
        }
        return false;
    }


    public void setDeck(Deck deck) {
        this.deck = deck;
    }
    public String playRound() {
        if (deck.getCards().size() < 6) {
            deck = new Deck(); // Új pakli, ha kevés lap van
        }

        Card playerCard1 = deck.draw();
        Card playerCard2 = deck.draw();
        Card bankerCard1 = deck.draw();
        Card bankerCard2 = deck.draw();

        int playerTotal = calculateTotal(playerCard1, playerCard2);
        int bankerTotal = calculateTotal(bankerCard1, bankerCard2);

        // "Természetes" győzelem ellenőrzése
        if (playerTotal >= 8 || bankerTotal >= 8) {
            lastResult = naturalWinResult(playerTotal, bankerTotal);
            updateChipsBasedOnResult();
            return lastResult;
        }

        Card playerThirdCard = null;
        if (playerTotal <= 5) {
            playerThirdCard = deck.draw();
            playerTotal = calculateTotalWithCard(playerTotal, playerThirdCard);
        }

        // Hasonlóan a bankár esetében
        if (shouldBankerDraw(bankerTotal, playerTotal, playerThirdCard)) {
            Card bankerThirdCard = deck.draw();
            bankerTotal = calculateTotalWithCard(bankerTotal, bankerThirdCard);
        }

        lastResult = determineOutcome(playerTotal, bankerTotal);
        updateChipsBasedOnResult();
        return lastResult;
    }

    private void updateChipsBasedOnResult() {
        // Feltételezzük, hogy a nyeremény kétszerese a tétnek, kivéve döntetlen esetén
        if ((betType.equals("player") && lastResult.contains("Játékos nyert")) ||
                (betType.equals("banker") && lastResult.contains("Bankár nyert"))) {
            player.win(betAmount * 2);
        } else if (betType.equals("tie") && lastResult.contains("Döntetlen")) {
            player.win(betAmount * 2); // vagy egyedi logika döntetlen esetén
        } else {
            player.lose(betAmount);
        }
        // Visszaállítjuk a tét típusát és összegét, kész a következő körre
        betType = "";
        betAmount = 0;
    }

    private boolean shouldBankerDraw(int bankerTotal, int playerTotal, Card playerThirdCard) {
        if (bankerTotal >= 7) {
            return false;
        }
        if (bankerTotal <= 2) {
            return true;
        }

        int playerThirdCardValue = playerThirdCard != null ? playerThirdCard.getPoints() : -1;

        // Bankár harmadik lapjának döntési logikája, figyelembe véve a játékos harmadik lapját
        if (playerThirdCardValue == -1) { // Ha a játékos nem húzott harmadik lapot
            return true;
        }
        switch (bankerTotal) {
            case 3:
                return playerThirdCardValue != 8;
            case 4:
                return playerThirdCardValue >= 2 && playerThirdCardValue <= 7;
            case 5:
                return playerThirdCardValue >= 4 && playerThirdCardValue <= 7;
            case 6:
                return playerThirdCardValue >= 6 && playerThirdCardValue <= 7;
            default:
                return false;
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
        if (playerTotal > bankerTotal) {
            return "Természetes győzelem! Játékos nyert! Pontszám: " + playerTotal + " vs. Bankár pontszáma: " + bankerTotal;
        } else if (bankerTotal > playerTotal) {
            return "Természetes győzelem! Bankár nyert! Pontszám: " + bankerTotal + " vs. Játékos pontszáma: " + playerTotal;
        } else {
            return "Döntetlen! Mindkét fél pontszáma: " + playerTotal;
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