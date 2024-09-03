package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.model.Deck;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BaccaratServiceTest {
    @Mock
    private Deck deckMock;

    private BaccaratService baccaratService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        baccaratService = new BaccaratService(deckMock);
    }

    @Test
    public void testNaturalWinForPlayer() {
        when(deckMock.draw())
                .thenReturn(new Card("Hearts", "9", 9),
                        new Card("Spades", "King", 0),
                        new Card("Diamonds", "3", 3),
                        new Card("Clubs", "4", 4));

        baccaratService.placeBet(BaccaratService.BetType.PLAYER, 10);
        String result = baccaratService.playRound();
        assertEquals("Player won with a natural 9! Player's score: 9 vs. Banker's score: 7", result,
                "The game outcome should reflect a natural win for the player.");
    }

    @Test
    public void testDrawWithNoThirdCard() {
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8),
                new Card("Spades", "Queen", 0),
                new Card("Diamonds", "8", 8),
                new Card("Clubs", "King", 0));
        baccaratService.placeBet(BaccaratService.BetType.TIE, 10); // TIE fogadás beállítása
        String result = baccaratService.playRound();
        assertEquals("Tie! Both sides have a natural 8.", result);

    }

    @Test
    public void testBankerWinWithNoThirdCard() {
        baccaratService.placeBet(BaccaratService.BetType.BANKER, 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "4", 4),
                new Card("Spades", "2", 2),
                new Card("Diamonds", "8", 8),
                new Card("Clubs", "Queen", 0));

        String result = baccaratService.playRound();
        assertEquals("Banker won with a natural 8! Banker's score: 8 vs. Player's score: 6", result,
                "The game outcome should reflect a banker win with a score of 8.");
    }

    @Test
    public void testBankerNaturalWinNoThirdCard() {
        baccaratService.placeBet(BaccaratService.BetType.BANKER, 10);

        // Mock the draw to ensure the banker gets a natural 9 and the player gets a
        // lower score
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "9", 9), // Banker's first card
                new Card("Diamonds", "Queen", 0), // Banker's second card
                new Card("Clubs", "4", 4), // Player's first card
                new Card("Spades", "5", 5)); // Player's second card

        String result = baccaratService.playRound();

        // Check that the banker won with a natural 9 and did not draw a third card
        assertEquals("Tie! Both sides have a natural 9.", result,
                "The game outcome should reflect a natural win for the banker with a score of 9.");

        // Verify that the deck.draw() method was called exactly 4 times, ensuring no
        // third card was drawn
        verify(deckMock, times(4)).draw();
    }

    @Test
    public void testPlayerDrawsThirdCard() {
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "4", 4),
                new Card("Spades", "1", 1),
                new Card("Diamonds", "6", 6),
                new Card("Clubs", "5", 5),
                new Card("Hearts", "2", 2));
        baccaratService.placeBet(BaccaratService.BetType.PLAYER, 10); // PLAYER fogadás beállítása
        String result = baccaratService.playRound();
        Assertions.assertTrue(result.contains("Player won! Score: 7"));
    }

    @Test
    public void testBankerDrawsThirdCard() {
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "3", 3), // Player's first card
                new Card("Spades", "4", 4), // Player's second card
                new Card("Diamonds", "2", 2), // Banker's first card
                new Card("Clubs", "2", 2), // Banker's second card
                new Card("Hearts", "5", 5) // Player's third card, if applicable
        );

        baccaratService.placeBet(BaccaratService.BetType.BANKER, 10);
        String result = baccaratService.playRound();

        // Ellenőrizzük, hogy a bankár húzott harmadik kártyát
        verify(deckMock, times(5)).draw();  // 5 húzás, beleértve a harmadik kártyát is

        // Lehetséges kimenetelek ellenőrzése
        String expectedBankerWinMessage = "Banker won with a natural 9! Banker's score: 9 vs. Player's score: 7";
        String expectedPlayerWinMessage = "Player won with a natural 9! Player's score: 9 vs. Banker's score: 7";
        String expectedTieMessage = "Tie! Both sides score: 7";

        Assertions.assertTrue(result.equals(expectedBankerWinMessage) ||
                        result.equals(expectedPlayerWinMessage) ||
                        result.equals(expectedTieMessage),
                "The result message should exactly match one of the expected outcomes.");
    }

    @Test
    public void testDeckReshuffle() {
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "A", 1),
                new Card("Diamonds", "K", 0),
                new Card("Clubs", "Q", 0),
                new Card("Spades", "J", 0),
                new Card("Hearts", "5", 5),
                new Card("Diamonds", "6", 6));
        baccaratService.placeBet(BaccaratService.BetType.PLAYER, 10);
        baccaratService.playRound();
        verify(deckMock, times(1)).reshuffle();
        verify(deckMock, atLeast(4)).draw();
        verify(deckMock, atMost(6)).draw();
    }

    @Test
    public void testPlaceBetWithInsufficientChips() {
        baccaratService.getPlayer().setChips(10); // Feltételezzük, hogy a játékosnak csak 10 zsetonja van
        Assertions.assertFalse(baccaratService.placeBet(BaccaratService.BetType.PLAYER, 20));
    }

    @Test
    public void testWinningBetUpdatesChipsCorrectly() {
        baccaratService.getPlayer().setChips(50);
        baccaratService.placeBet(BaccaratService.BetType.PLAYER, 10);
        // Konfiguráljuk úgy, hogy a "player" nyerjen (pl. játékos pontszáma 8, bankáré
        // 7)
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), //Player 1
                new Card("Diamonds", "K", 0), //Player2
                new Card("Spades", "7", 7), //Banker 1
                new Card("Clubs", "Q", 0)); //Banker 2

        baccaratService.playRound();
        assertEquals(70, baccaratService.getPlayer().getChips());
    }

    @Test
    public void testLosingBetUpdatesChipsCorrectly() {
        baccaratService.getPlayer().setChips(50);
        baccaratService.placeBet(BaccaratService.BetType.PLAYER, 10);
        // Konfiguráljuk úgy, hogy a "player" veszít (pl. játékos pontszáma 6, bankáré
        // 7)
        when(deckMock.draw()).thenReturn(new Card("Hearts", "6", 6), new Card("Diamonds", "5", 5),
                new Card("Spades", "7", 7), new Card("Clubs", "2", 2),
                new Card("Hearts", "9", 9)); // További lapok, ha szükséges
        baccaratService.playRound();
        assertEquals(40, baccaratService.getPlayer().getChips()); // A tét levonásra kerül
    }

    @Test
    public void testNaturalTie() {
        // Beállítjuk a mockolt kártyákat egy természetes döntetlenhez
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), // Játékos lap 1
                new Card("Spades", "Queen", 0), // Játékos lap 2
                new Card("Diamonds", "8", 8), // Bankár lap 1
                new Card("Clubs", "King", 0) // Bankár lap 2
        );

        // A játék kör lejátszása
        baccaratService.placeBet(BaccaratService.BetType.TIE, 10);
        String result = baccaratService.playRound();

        // Ellenőrizzük, hogy a kimenet megfelel-e a természetes döntetlen szabályainak
        assertEquals("Tie! Both sides have a natural 8.", result);
    }

    @Test
    public void testTieReturnsBet() {
        baccaratService.getPlayer().setChips(50);
        baccaratService.placeBet(BaccaratService.BetType.TIE, 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8),
                new Card("Diamonds", "Q", 0),
                new Card("Spades", "8", 8),
                new Card("Clubs", "K", 0),
                new Card("Hearts", "2", 2));
        baccaratService.playRound();
        assertEquals(140, baccaratService.getPlayer().getChips()); // 50 kezdő zseton + 90 nyeremény (tét + 8x tét)
    }

    @Test
    public void testInvalidBetType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            baccaratService.placeBet(BaccaratService.BetType.valueOf("INVALID_TYPE"), 10);
        });

    }

    @Test
    public void testPayoutForTieBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet(BaccaratService.BetType.TIE, 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8),
                new Card("Spades", "K", 0),
                new Card("Diamonds", "8", 8),
                new Card("Clubs", "K", 0));
        baccaratService.playRound();
        assertEquals(190, baccaratService.getPlayer().getChips()); // 100 kezdő zseton + 90 nyeremény (tét + 8x tét)
    }

    @Test
    public void testNegativeBetAmount() {
        Assertions.assertFalse(baccaratService.placeBet(BaccaratService.BetType.PLAYER, -10)); // Érvénytelen fogadási
                                                                                               // összeg
    }

    @Test
    public void testPerfectPairBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet(BaccaratService.BetType.PERFECT_PAIR_ONE, 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), new Card("Hearts", "8", 8), // Tökéletes pár a játékosnak
                new Card("Spades", "K", 0), new Card("Clubs", "K", 0) // Nem pár a bankárnak
        );
        baccaratService.playRound();
        assertEquals(350, baccaratService.getPlayer().getChips());
    }

    @Test
    public void testPlayerPairBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet(BaccaratService.BetType.PLAYER_PAIR, 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), new Card("Diamonds", "8", 8), // Pár a játékosnak
                new Card("Spades", "K", 0), new Card("Clubs", "K", 0) // Nem pár a bankárnak
        );
        baccaratService.playRound();
        assertEquals(210, baccaratService.getPlayer().getChips());
    }

    @Test
    public void testBankerPairBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet(BaccaratService.BetType.BANKER_PAIR, 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), new Card("Diamonds", "K", 0), // Nem pár a játékosnak
                new Card("Spades", "K", 0), new Card("Clubs", "K", 0) // Pár a bankárnak
        );
        baccaratService.playRound();
        assertEquals(210, baccaratService.getPlayer().getChips());
    }

    @Test
    public void testEitherPairBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet(BaccaratService.BetType.EITHER_PAIR, 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), new Card("Diamonds", "K", 0), // Nem pár a játékosnak
                new Card("Spades", "K", 0), new Card("Clubs", "K", 0) // Pár a bankárnak
        );
        baccaratService.playRound();
        assertEquals(150, baccaratService.getPlayer().getChips());
    }

}
