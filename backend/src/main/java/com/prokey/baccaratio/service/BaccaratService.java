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
        if (amount <= 0 || (!type.equals("player") && !type.equals("banker") && !type.equals("tie"))) {
            return false; // Invalid bet amount or type
        }
        if (this.player.getChips() < amount) {
            return false; // Insufficient chips
        }
        this.betType = type;
        this.betAmount = amount;
        return true;
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
                    // Considering the standard 5% commission on banker wins
                    payout = (int) (this.betAmount * 1.95);
                    isWin = true;
                }
                break;
            case "tie":
                if (this.lastResult.startsWith("Döntetlen")) {
                    payout = this.betAmount * 8; // Tie bets typically offer 8:1 payout
                    isWin = true;
                }
                break;
            default:
                break; // No additional action required for default case
        }

// Inside updateChipsBasedOnResult
        if (isWin && this.betType.equals("tie")) {
            this.player.win(payout + this.betAmount); // Ensure the original bet is also returned in case of a tie
        } else if (isWin) {
            this.player.win(payout);
        } else {
            this.player.lose(this.betAmount);
        }

        // Reset bet for next round
        this.betType = "";
        this.betAmount = 0;
    }

    private boolean shouldBankerDraw(int bankerTotal, int playerTotal, Card playerThirdCard) {
        if (bankerTotal >= 7) {
            return false; // Banker stands
        }

        if (bankerTotal <= 2) {
            return true; // Banker always draws if the total is 2 or less
        }

        int playerThirdCardValue = playerThirdCard != null ? playerThirdCard.getPoints() : -1;

        if (bankerTotal == 3) {
            return playerThirdCardValue != 8; // Banker draws unless the player's third card is an 8
        } else if (bankerTotal == 4) {
            return playerThirdCardValue >= 2 && playerThirdCardValue <= 7;
        } else if (bankerTotal == 5) {
            return playerThirdCardValue >= 4 && playerThirdCardValue <= 7;
        } else if (bankerTotal == 6) {
            return playerThirdCardValue == 6 || playerThirdCardValue == 7;
        }

        return false; // Default case if none of the above conditions are met
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

    // Adjusting naturalWinResult logic for clarity
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
    private int calculateTotalWithThirdCard(int currentTotal, Card thirdCard) {
        // Ha van harmadik lap, annak értékét is hozzáadjuk az összeghez
        if (thirdCard != null) {
            return (currentTotal + thirdCard.getPoints()) % 10;
        }
        return currentTotal;
    }
}