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

        playerCards = new ArrayList<>();
        bankerCards = new ArrayList<>();

        // A kártyák húzása
        playerCards.add(deck.draw());
        playerCards.add(deck.draw());
        bankerCards.add(deck.draw());
        bankerCards.add(deck.draw());

        // Reshuffle, ha kevesebb, mint 6 kártya maradt
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

        // Ellenőrizzük, hogy a játékosnak van-e még zsetonja
        if (this.player.getChips() <= 0) {
            return "Elfogytak a zsetonjaid. Vesztettél.";
        }

        return lastResult;
    }

    private void updateChipsBasedOnResult() {
        // Az első két kártya kinyerése a listákból
        Card playerCard1 = playerCards.size() > 0 ? playerCards.get(0) : null;
        Card playerCard2 = playerCards.size() > 1 ? playerCards.get(1) : null;
        Card bankerCard1 = bankerCards.size() > 0 ? bankerCards.get(0) : null;
        Card bankerCard2 = bankerCards.size() > 1 ? bankerCards.get(1) : null;

        // Esetleges harmadik kártyák ellenőrzése
        Card playerThirdCard = playerCards.size() > 2 ? playerCards.get(2) : null;
        Card bankerThirdCard = bankerCards.size() > 2 ? bankerCards.get(2) : null;

        int payout = 0;
        boolean isWin = false;

        // A fogadási típusok és a győztesek logikája
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
            // A bankár összpontszáma 7 vagy magasabb: nem húz további lapot.
            return false;
        } else if (bankerTotal >= 3 && bankerTotal <= 6) {
            // Speciális szabályok érvényesek, ha a bankár összpontszáma 3 és 6 között van.
            if (playerThirdCard == null) {
                // Ha a játékos nem húzott harmadik lapot, a bankár akkor húz,
                // ha az összértéke 5 vagy kevesebb.
                return bankerTotal <= 5;
            } else {
                // A játékos húzott harmadik lapot, így a döntés a lap értékétől függ.
                int playerThirdCardValue = playerThirdCard.getPoints();
                switch (bankerTotal) {
                    case 3:
                        // A bankár húz, kivéve, ha a játékos harmadik lapja 8.
                        return playerThirdCardValue != 8;
                    case 4:
                        // A bankár húz, ha a játékos harmadik lapja 2-7 között van.
                        return playerThirdCardValue >= 2 && playerThirdCardValue <= 7;
                    case 5:
                        // A bankár húz, ha a játékos harmadik lapja 4-7 között van.
                        return playerThirdCardValue >= 4 && playerThirdCardValue <= 7;
                    case 6:
                        // A bankár húz, ha a játékos harmadik lapja 6 vagy 7.
                        return playerThirdCardValue == 6 || playerThirdCardValue == 7;
                    default:
                        // Minden más esetben a bankár nem húz.
                        return false;
                }
            }
        } else {
            // Ha a bankár összpontszáma 0, 1, vagy 2, mindig húz egy lapot.
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

    public List<Card> getPlayerCards() {
        return playerCards;
    }

    public List<Card> getBankerCards() {
        return bankerCards;
    }

    public boolean updateChips(int amount) {
        // Implementáció, hogy frissítsük a játékos zsetonjainak számát
        if (player != null) {
            player.setChips(player.getChips() + amount);
            return true;
        }
        return false;
    }
}

