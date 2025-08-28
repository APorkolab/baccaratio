package com.prokey.baccaratio.model;

import com.prokey.baccaratio.service.BaccaratService.BetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameTest {

    private Player player;

    @Mock
    private Deck deck;

    @BeforeEach
    void setUp() {
        player = new Player("testuser", 1000);
    }

    @Test
    void testPlayerPairWin() {
        when(deck.draw()).thenReturn(
                new Card("Hearts", "A", 1),
                new Card("Diamonds", "2", 2),
                new Card("Clubs", "A", 1),
                new Card("Spades", "3", 3)
        );
        Game game = new Game(player, deck);
        game.placeBet(BetType.PLAYERPAIR, 100);
        game.playRound();
        assertEquals(2100, player.getChips());
    }

    @Test
    void testBankerPairWin() {
        when(deck.draw()).thenReturn(
                new Card("Hearts", "2", 2),
                new Card("Clubs", "A", 1),
                new Card("Diamonds", "3", 3),
                new Card("Spades", "A", 1)
        );
        Game game = new Game(player, deck);
        game.placeBet(BetType.BANKERPAIR, 100);
        game.playRound();
        assertEquals(2100, player.getChips());
    }

    @Test
    void testEitherPairWin() {
        when(deck.draw()).thenReturn(
                new Card("Hearts", "A", 1),
                new Card("Diamonds", "2", 2),
                new Card("Clubs", "A", 1),
                new Card("Spades", "3", 3)
        );
        Game game = new Game(player, deck);
        game.placeBet(BetType.EITHERPAIR, 100);
        game.playRound();
        assertEquals(1500, player.getChips());
    }

    @Test
    void testPerfectPairWin() {
        when(deck.draw()).thenReturn(
                new Card("Hearts", "A", 1),
                new Card("Clubs", "2", 2),
                new Card("Hearts", "A", 1),
                new Card("Spades", "3", 3)
        );
        Game game = new Game(player, deck);
        game.placeBet(BetType.PERFECTPAIRONE, 100);
        game.playRound();
        assertEquals(3500, player.getChips());
    }
}
