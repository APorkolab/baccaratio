package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.model.Deck;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

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
        // Setup the scenario for a natural win: Player scores 9, and Banker scores less, on the first two cards.
        when(deckMock.draw())
                .thenReturn(new Card("Hearts", "9", 9), // Player's first card
                        new Card("Spades", "0", 0),  // Player's second card, ensuring a total of 9
                        new Card("Diamonds", "3", 3), // Banker's first card
                        new Card("Clubs", "2", 2));   // Banker's second card, ensuring a total less than Player's

        String result = baccaratService.playRound();
        assertEquals("Player won! Score: 9 vs. Banker's score: 5", result, "The game outcome should reflect a natural win for the player.");
    }

    @Test
    public void testDrawWithNoThirdCard() {
        when(deckMock.draw()).thenReturn(new Card("Hearts", "8", 8), new Card("Spades", "8", 8),
                new Card("Diamonds", "8", 8), new Card("Clubs", "8", 8));
        String result = baccaratService.playRound();
        assertEquals("Tie! Both sides score: 6", result);
    }

    @Test
    public void testPlayerWinWithNoThirdCard() {
        // Mock the deck to return specific cards to ensure the player wins without a third card
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "9", 9), // Player Card 1
                new Card("Spades", "2", 2), // Player Card 2
                new Card("Diamonds", "7", 7), // Banker Card 1
                new Card("Clubs", "2", 2) // Banker Card 2
        );

        // Execute the round
        String result = baccaratService.playRound();

        // Verify the outcome is a banker win due to a natural 9
        assertEquals("Banker won! Score: 9 vs. Player's score: 1", result);
    }


    @Test
    public void testPlayerDrawsThirdCard() {
        when(deckMock.draw()).thenReturn(new Card("Hearts", "4", 4), new Card("Spades", "1", 1),
                new Card("Diamonds", "6", 6), new Card("Clubs", "5", 5), new Card("Hearts", "2", 2));
        String result = baccaratService.playRound();
        Assertions.assertTrue(result.contains("Player won! Score: 7")); // Feltételezve, hogy a harmadik lap a játékos számára kedvező
    }

    @Test
    public void testBankerDrawsThirdCard() {
        // Setup scenario: Player and Banker have initial totals that require decision making for a third card
        when(deckMock.draw())
                .thenReturn(new Card("Hearts", "2", 2), // Player's first card
                        new Card("Spades", "3", 3),  // Player's second card
                        new Card("Diamonds", "4", 4), // Banker's first card
                        new Card("Clubs", "2", 2),   // Banker's second card
                        new Card("Hearts", "5", 5)); // Player's third card, if applicable

        String result = baccaratService.playRound();
        Assertions.assertTrue(result.startsWith("Banker won!"), "The outcome should reflect that the Banker drew a third card and won or the game logic led to a Banker win.");

    }

    @Test
    public void testDeckReshuffle() {
        when(deckMock.size()).thenReturn(4, 52); // Kezdetben 4 lap van, újrakeverés után 52
        when(deckMock.draw()).thenReturn(new Card("Hearts", "8", 8), new Card("Spades", "8", 8),
                new Card("Diamonds", "9", 9), new Card("Clubs", "9", 9));
        baccaratService.playRound(); // Első kör, amely után újra kell keverni a paklit
        verify(deckMock, times(1)).reshuffle(); // Ellenőrizzük, hogy meghívódott-e az újrakeverés
    }

    @Test
    public void testPlaceBetWithInsufficientChips() {
        baccaratService.getPlayer().setChips(10); // Feltételezzük, hogy a játékosnak csak 10 zsetonja van
        Assertions.assertFalse(baccaratService.placeBet("player", 20)); // A tét 20, ami több, mint a játékos zsetonjai
    }

    @Test
    public void testWinningBetUpdatesChipsCorrectly() {
        baccaratService.getPlayer().setChips(50);
        baccaratService.placeBet("player", 10);
        // Konfiguráljuk úgy, hogy a "player" nyerjen (pl. játékos pontszáma 8, bankáré 7)
        when(deckMock.draw()).thenReturn(new Card("Hearts", "8", 8), new Card("Diamonds", "K", 0),
                new Card("Spades", "7", 7), new Card("Clubs", "Q", 0),
                new Card("Hearts", "2", 2));
        baccaratService.playRound();
        assertEquals(70, baccaratService.getPlayer().getChips());
    }
    @Test
    public void testLosingBetUpdatesChipsCorrectly() {
        baccaratService.getPlayer().setChips(50);
        baccaratService.placeBet("player", 10);
        // Konfiguráljuk úgy, hogy a "player" veszít (pl. játékos pontszáma 6, bankáré 7)
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
                new Card("Hearts", "8", 8),  // Játékos lap 1
                new Card("Spades", "Queen", 0),  // Játékos lap 2
                new Card("Diamonds", "8", 8),  // Bankár lap 1
                new Card("Clubs", "King", 0)    // Bankár lap 2
        );

        // A játék kör lejátszása
        String result = baccaratService.playRound();

        // Ellenőrizzük, hogy a kimenet megfelel-e a természetes döntetlen szabályainak
        assertEquals("Tie! Both sides score: 8", result);
    }

    @Test
    public void testTieReturnsBet() {
        baccaratService.getPlayer().setChips(50);
        baccaratService.placeBet("tie", 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8),
                new Card("Diamonds", "Q", 0),
                new Card("Spades", "8", 8),
                new Card("Clubs", "K", 0),
                new Card("Hearts", "2", 2)
        );
        baccaratService.playRound();
        assertEquals(140, baccaratService.getPlayer().getChips()); // 50 kezdő zseton + 90 nyeremény (tét + 8x tét)
    }

    @Test
    public void testInvalidBetType() {
        Assertions.assertFalse(baccaratService.placeBet("invalid_type", 10)); // Érvénytelen fogadási típus
    }

    @Test
    public void testPayoutForTieBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet("tie", 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8),
                new Card("Spades", "K", 0),
                new Card("Diamonds", "8", 8),
                new Card("Clubs", "K", 0)
        );
        baccaratService.playRound();
        assertEquals(190, baccaratService.getPlayer().getChips()); // 100 kezdő zseton + 90 nyeremény (tét + 8x tét)
    }

    @Test
    public void testNegativeBetAmount() {
        Assertions.assertFalse(baccaratService.placeBet("player", -10)); // Érvénytelen fogadási összeg
    }

    @Test
    public void testPerfectPairBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet("perfectPairOne", 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), new Card("Hearts", "8", 8),  // Tökéletes pár a játékosnak
                new Card("Spades", "K", 0), new Card("Clubs", "K", 0)   // Nem pár a bankárnak
        );
        baccaratService.playRound();
        assertEquals(350, baccaratService.getPlayer().getChips());
    }

    @Test
    public void testPlayerPairBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet("playerPair", 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), new Card("Diamonds", "8", 8), // Pár a játékosnak
                new Card("Spades", "K", 0), new Card("Clubs", "K", 0)    // Nem pár a bankárnak
        );
        baccaratService.playRound();
        assertEquals(210, baccaratService.getPlayer().getChips());
    }

    @Test
    public void testBankerPairBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet("bankerPair", 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), new Card("Diamonds", "K", 0), // Nem pár a játékosnak
                new Card("Spades", "K", 0), new Card("Clubs", "K", 0)    // Pár a bankárnak
        );
        baccaratService.playRound();
        assertEquals(210, baccaratService.getPlayer().getChips());
    }

    @Test
    public void testEitherPairBet() {
        baccaratService.getPlayer().setChips(100);
        baccaratService.placeBet("eitherPair", 10);
        when(deckMock.draw()).thenReturn(
                new Card("Hearts", "8", 8), new Card("Diamonds", "K", 0), // Nem pár a játékosnak
                new Card("Spades", "K", 0), new Card("Clubs", "K", 0)    // Pár a bankárnak
        );
        baccaratService.playRound();
        assertEquals(150, baccaratService.getPlayer().getChips());
    }

}
