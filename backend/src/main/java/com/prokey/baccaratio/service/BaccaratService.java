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
        this.player = new Player("Default Player", 100); // Kezdeti zsetonok száma
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean placeBet(String type, int amount) {
        if (amount <= 0) {
            return false; // Érvénytelen fogadási összeg
        }
        if (!type.equals("player") && !type.equals("banker") && !type.equals("tie")) {
            return false; // Érvénytelen fogadási típus
        }
        if (player.getChips() >= amount) {
            this.betType = type;
            this.betAmount = amount;
            // Itt további logika lehet, ha szükséges
            return true;
        } else {
            return false; // Nincs elegendő zseton a fogadásra
        }
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public String playRound(Card playerCard1, Card playerCard2, Card bankerCard1, Card bankerCard2) {
        if (deck.getCards().size() < 6) {
            deck.reshuffle();
        }

        int playerTotal = calculateTotal(playerCard1, playerCard2);
        int bankerTotal = calculateTotal(bankerCard1, bankerCard2);

        // "Természetes" győzelem ellenőrzése
        if (playerTotal == 8 || playerTotal == 9 || bankerTotal == 8 || bankerTotal == 9) {
            lastResult = naturalWinResult(playerTotal, bankerTotal);
        } else {
            // További logika a harmadik kártya húzására
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
        updateChipsBasedOnResult();
        return lastResult;
    }

    public String playRound() {
        // Alapértelmezett értékekkel rendelkező Card objektumok létrehozása
        Card playerCard1 = deck.draw();
        Card playerCard2 = deck.draw();
        Card bankerCard1 = deck.draw();
        Card bankerCard2 = deck.draw();

        return playRound(playerCard1, playerCard2, bankerCard1, bankerCard2);
    }

    private void updateChipsBasedOnResult() {
        switch (betType) {
            case "player":
                if (lastResult.contains("Játékos nyert")) player.win(betAmount * 2);
                break;
            case "banker":
                if (lastResult.contains("Bankár nyert")) player.win((int)(betAmount * 1.95)); // Bankári győzelem esetén gyakran van egy 5% commission
                break;
            case "tie":
                if (lastResult.contains("Döntetlen")) player.win(betAmount * 8); // A döntetlen esetében magasabb kifizetés
                break;
            default:
                player.lose(betAmount);
                break;
        }
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
                return playerThirdCardValue == 6 || playerThirdCardValue == 7;
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
        if (playerTotal == bankerTotal) {
            return "Döntetlen! Mindkét fél pontszáma: " + playerTotal; // Eredeti hiba javítása
        } else if (playerTotal > bankerTotal) {
            return "Játékos nyert! Pontszám: " + playerTotal + " vs. Bankár pontszáma: " + bankerTotal;
        }
        else {
            return "Bankár nyert! Pontszám: " + bankerTotal + " vs. Játékos pontszáma: " + playerTotal;
        }
    }

    private String determineOutcome(int playerTotal, int bankerTotal) {
        if (playerTotal > bankerTotal) {
            return "Játékos nyert! Pontszám: " + playerTotal + " vs. Bankár pontszáma: " + bankerTotal;
        } else if (bankerTotal > playerTotal) {
            return "Bankár nyert! Pontszám: " + bankerTotal + " vs. Játékos pontszáma: " + playerTotal;
        } else {
            return "Döntetlen! Mindkét fél pontszáma: " + playerTotal; // Itt egyeztetés szükséges, ha a döntetlen nem a játékos pontszámát kellene megjeleníteni
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
    private int calculateTotalWithThirdCard(int currentTotal, Card thirdCard) {
        // Ha van harmadik lap, annak értékét is hozzáadjuk az összeghez
        if (thirdCard != null) {
            return (currentTotal + thirdCard.getPoints()) % 10;
        }
        return currentTotal;
    }
}