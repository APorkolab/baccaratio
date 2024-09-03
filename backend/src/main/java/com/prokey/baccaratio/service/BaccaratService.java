package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.model.Deck;
import com.prokey.baccaratio.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BaccaratService {
    private Deck deck;
    private Player player;
    private String lastResult = "";
    private BetType betType;
    private int betAmount = 0;
    private List<Card> playerCards = new ArrayList<>();
    private List<Card> bankerCards = new ArrayList<>();
    int payout = 0;
    boolean isWin = false;

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

    public boolean placeBet(BetType type, int amount) {
        if (amount <= 0 || this.player.getChips() < amount) {
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
        if (this.betAmount == 0 || this.betType == null) {
            return "Fogadást kell tennie a kör megkezdése előtt.";
        }
        if (this.betAmount > this.player.getChips()) {
            return "Nincs elég zsetonja ehhez a fogadáshoz. A játék nem indulhat el.";
        }

        deck.reshuffle();
        resetRound();
        drawInitialCards();

        int playerTotal = calculateTotal(playerCards);
        int bankerTotal = calculateTotal(bankerCards);

        // Ellenőrizze a természetes győzelmet
        if (playerTotal >= 8 || bankerTotal >= 8) {
            lastResult = determineOutcome(playerTotal, bankerTotal);
            updateChipsBasedOnResult();
            return lastResult;
        }

        // Ha nincs természetes győzelem, folytassa a játékmenetet
        drawAdditionalCards(playerTotal, bankerTotal);
        playerTotal = calculateTotal(playerCards);
        bankerTotal = calculateTotal(bankerCards);

        lastResult = determineOutcome(playerTotal, bankerTotal);
        updateChipsBasedOnResult();

        if (this.player.getChips() <= 0) {
            return "Elfogytak a zsetonjai. Vesztett.";
        }

        return lastResult;
    }

    private void resetRound() {
        payout = 0;
        isWin = false;
        playerCards.clear();
        bankerCards.clear();
    }

    private void drawInitialCards() {
        playerCards.add(deck.draw());
        playerCards.add(deck.draw());
        bankerCards.add(deck.draw());
        bankerCards.add(deck.draw());
    }

    private void drawAdditionalCards(int playerTotal, int bankerTotal) {
        if (playerTotal <= 5) {
            playerCards.add(deck.draw());
            playerTotal = calculateTotal(playerCards);
        }

        if (bankerTotal <= 5) {
            if (playerCards.size() == 2
                    || (playerCards.size() == 3 && shouldBankerDraw(bankerTotal, playerCards.get(2).getPoints()))) {
                bankerCards.add(deck.draw());
            }
        }
    }

    private boolean shouldBankerDraw(int bankerTotal, int playerThirdCardValue) {
        if (bankerTotal <= 2)
            return true;
        if (bankerTotal == 3 && playerThirdCardValue != 8)
            return true;
        if (bankerTotal == 4 && (playerThirdCardValue >= 2 && playerThirdCardValue <= 7))
            return true;
        if (bankerTotal == 5 && (playerThirdCardValue >= 4 && playerThirdCardValue <= 7))
            return true;
        if (bankerTotal == 6 && (playerThirdCardValue == 6 || playerThirdCardValue == 7))
            return true;
        return false;
    }

    public synchronized void updateChipsBasedOnResult() {
        payout = calculatePayout(
                playerCards.size() > 0 ? playerCards.get(0) : null,
                playerCards.size() > 1 ? playerCards.get(1) : null,
                bankerCards.size() > 0 ? bankerCards.get(0) : null,
                bankerCards.size() > 1 ? bankerCards.get(1) : null);

        if (this.lastResult.contains("Tie!")) {
            handleTieResult();
        } else if (payout > 0) {
            // Ha a játékos nyer, adjuk hozzá a tétet és a nyereményt a zsetonjaihoz
            this.player.win(payout + this.betAmount);
        } else {
            // Ha a játékos veszít, vonjuk le a tétet
            this.player.lose(this.betAmount);
        }
    }

    private int calculatePayout(Card playerCard1, Card playerCard2, Card bankerCard1, Card bankerCard2) {
        switch (this.betType) {
            case PLAYER:
                return this.lastResult.contains("Player won!") ? this.betAmount * 2 : 0;
            case BANKER:
                return this.lastResult.contains("Banker won!") ? (int) (this.betAmount * 1.95) : 0;
            case TIE:
                return this.lastResult.contains("Tie!") ? this.betAmount * 8 : 0;
            case PERFECT_PAIR_ONE:
                return (isPerfectPair(playerCard1, playerCard2) || isPerfectPair(bankerCard1, bankerCard2))
                        ? this.betAmount * 25
                        : 0;
            case PLAYER_PAIR:
                return isPair(playerCard1, playerCard2) ? this.betAmount * 11 : 0;
            case BANKER_PAIR:
                return isPair(bankerCard1, bankerCard2) ? this.betAmount * 11 : 0;
            case EITHER_PAIR:
                return (isPair(playerCard1, playerCard2) || isPair(bankerCard1, bankerCard2)) ? this.betAmount * 5 : 0;
            default:
                return 0;
        }
    }

    private void handleTieResult() {
        if (this.betType == BetType.TIE) {
            this.player.win(this.payout + this.betAmount);
        } else {
            // Döntetlen esetén vissza kell adni a tétet, ha nem TIE-re fogadtak
            this.player.win(this.betAmount);
        }
    }

    private boolean isPerfectPair(Card card1, Card card2) {
        return card1.getSuit().equals(card2.getSuit()) && card1.getValue().equals(card2.getValue());
    }

    private boolean isPair(Card card1, Card card2) {
        return card1.getValue().equals(card2.getValue());
    }

    private int calculateTotal(List<Card> cards) {
        int total = cards.stream().mapToInt(Card::getPoints).sum();
        return total % 10;
    }

    private String determineOutcome(int playerTotal, int bankerTotal) {
        if (playerTotal == 9 && bankerTotal == 9) {
            return "Tie! Both sides have a natural 9.";
        } else if (playerTotal == 9) {
            return String.format("Player won with a natural 9! Player's score: 9 vs. Banker's score: %d", bankerTotal);
        } else if (bankerTotal == 9) {
            return String.format("Banker won with a natural 9! Banker's score: 9 vs. Player's score: %d", playerTotal);
        } else if (playerTotal == 8 && bankerTotal == 8) {
            return "Tie! Both sides have a natural 8.";
        } else if (playerTotal == 8) {
            return String.format("Player won with a natural 8! Player's score: 8 vs. Banker's score: %d", bankerTotal);
        } else if (bankerTotal == 8) {
            return String.format("Banker won with a natural 8! Banker's score: 8 vs. Player's score: %d", playerTotal);
        } else if (playerTotal == bankerTotal) {
            return String.format("Tie! Both sides score: %d", playerTotal);
        } else if (playerTotal > bankerTotal) {
            return String.format("Player won! Score: %d vs. Banker's score: %d", playerTotal, bankerTotal);
        } else {
            return String.format("Banker won! Score: %d vs. Player's score: %d", bankerTotal, playerTotal);
        }
    }

    public int getChips() {
        return player.getChips();
    }

    public String getLastResult() {
        return lastResult;
    }

    public BetType getBetType() {
        return betType;
    }

    public List<Card> getPlayerCards() {
        return new ArrayList<>(playerCards);
    }

    public List<Card> getBankerCards() {
        return new ArrayList<>(bankerCards);
    }

    public boolean updateChips(int amount) {
        if (player != null && amount != 0) {
            int newChips = player.getChips() + amount;
            if (newChips >= 0) {
                player.setChips(newChips);
                return true;
            }
        }
        return false;
    }

    public boolean isValidType(String type) {
        return Deck.isValidBetType(type);
    }

    public enum BetType {
        PLAYER,
        BANKER,
        TIE,
        PERFECT_PAIR_ONE,
        PLAYER_PAIR,
        BANKER_PAIR,
        EITHER_PAIR
    }
}
