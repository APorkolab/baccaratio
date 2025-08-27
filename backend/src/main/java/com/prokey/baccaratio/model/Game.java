package com.prokey.baccaratio.model;

import com.prokey.baccaratio.service.BaccaratService.BetType;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Deck deck;
    private final Player player;
    private String lastResult = "";
    private BetType betType;
    private int betAmount = 0;
    private final List<Card> playerCards = new ArrayList<>();
    private final List<Card> bankerCards = new ArrayList<>();

    public Game(Player player) {
        this.deck = new Deck();
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

    public String playRound() {
        if (this.betAmount == 0 || this.betType == null) {
            throw new IllegalStateException("A bet must be placed before starting the round.");
        }
        if (this.player.getChips() < this.betAmount) {
            throw new IllegalStateException("Insufficient chips for this bet.");
        }

        // Deduct chips at the start of the round
        this.player.lose(this.betAmount);

        resetRoundState();
        drawInitialCards();

        int playerTotal = calculateTotal(playerCards);
        int bankerTotal = calculateTotal(bankerCards);

        if (playerTotal >= 8 || bankerTotal >= 8) { // Natural win check
            lastResult = determineOutcome(playerTotal, bankerTotal);
            updateChipsBasedOnResult(lastResult);
            return lastResult;
        }

        drawAdditionalCards(playerTotal, bankerTotal);
        playerTotal = calculateTotal(playerCards);
        bankerTotal = calculateTotal(bankerCards);

        lastResult = determineOutcome(playerTotal, bankerTotal);
        updateChipsBasedOnResult(lastResult);

        if (this.player.getChips() <= 0) {
            lastResult += " You have run out of chips.";
        }

        return lastResult;
    }

    private void resetRoundState() {
        playerCards.clear();
        bankerCards.clear();
        if (deck.getCards().size() < 15) { // Reshuffle if deck is low
            deck.reshuffle();
        }
    }

    private void drawInitialCards() {
        playerCards.add(deck.draw());
        bankerCards.add(deck.draw());
        playerCards.add(deck.draw());
        bankerCards.add(deck.draw());
    }

    private int calculateTotal(List<Card> cards) {
        return cards.stream().mapToInt(Card::getPoints).sum() % 10;
    }

    private void drawAdditionalCards(int playerTotal, int bankerTotal) {
        boolean playerDrew = false;
        if (playerTotal <= 5) {
            playerCards.add(deck.draw());
            playerDrew = true;
        }

        // Must recalculate banker's total before deciding if they draw
        int newBankerTotal = calculateTotal(bankerCards);
        if (playerDrew) {
            // Player drew, banker's draw depends on more complex rules
            int playerThirdCardValue = playerCards.get(2).getPoints();
            if (shouldBankerDraw(newBankerTotal, playerThirdCardValue)) {
                bankerCards.add(deck.draw());
            }
        } else { // Player stands, banker draws on 5 or less
            if (newBankerTotal <= 5) {
                bankerCards.add(deck.draw());
            }
        }
    }

    private boolean shouldBankerDraw(int bankerTotal, int playerThirdCardValue) {
        return switch (bankerTotal) {
            case 0, 1, 2 -> true;
            case 3 -> playerThirdCardValue != 8;
            case 4 -> playerThirdCardValue >= 2 && playerThirdCardValue <= 7;
            case 5 -> playerThirdCardValue >= 4 && playerThirdCardValue <= 7;
            case 6 -> playerThirdCardValue == 6 || playerThirdCardValue == 7;
            default -> false;
        };
    }

    private String determineOutcome(int playerTotal, int bankerTotal) {
        if (playerTotal == bankerTotal) {
            return "Tie";
        } else if (playerTotal > bankerTotal) {
            return "Player";
        } else {
            return "Banker";
        }
    }

    private void updateChipsBasedOnResult(String winner) {
        boolean betWon = (betType == BetType.PLAYER && "Player".equals(winner)) ||
                         (betType == BetType.BANKER && "Banker".equals(winner)) ||
                         (betType == BetType.TIE && "Tie".equals(winner));

        if ("Tie".equals(winner)) {
            // On a Tie, the original bet is returned (push)
            player.win(betAmount);
            if (betType == BetType.TIE) {
                // If the bet was on Tie, pay winnings (8:1)
                player.win(betAmount * 8);
            }
        } else if (betWon) {
            if (betType == BetType.PLAYER) {
                // Pay 1:1, so return original bet + winning
                player.win(betAmount * 2);
            } else if (betType == BetType.BANKER) {
                // Pay 1:1 with 5% commission. Total return is bet * 1.95
                player.win((int) (betAmount * 1.95));
            }
        }
        // If bet was lost, chips were already deducted, so nothing to do.
    }

    // Getters for state
    public Player getPlayer() { return player; }
    public String getLastResult() { return lastResult; }
    public BetType getBetType() { return betType; }
    public int getBetAmount() { return betAmount; }
    public List<Card> getPlayerCards() { return new ArrayList<>(playerCards); }
    public List<Card> getBankerCards() { return new ArrayList<>(bankerCards); }
}
