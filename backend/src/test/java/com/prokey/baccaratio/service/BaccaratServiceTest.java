package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.model.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

public class BaccaratServiceTest {

    private BaccaratService baccaratService;
    private Deck deckMock;

    @BeforeEach
    public void setUp() {
        deckMock = Mockito.mock(Deck.class);
        baccaratService = new BaccaratService();
        baccaratService.setDeck(deckMock);
    }

    @Test
    public void testNaturalWinForPlayer() {
        // Feltételezzük, hogy a Deck mock visszaad konkrét értékeket a teszteléshez
        when(deckMock.draw()).thenReturn(new Card("Hearts", "9", 9), new Card("Spades", "7", 7),
                new Card("Diamonds", "3", 3), new Card("Clubs", "2", 2));
        String result = baccaratService.playRound();
        assertEquals("Természetes győzelem! Játékos nyert! Pontszám: 9 vs. Bankár pontszáma: 5", result);
    }

    @Test
    public void testDrawWithNoThirdCard() {
        when(deckMock.draw()).thenReturn(new Card("Hearts", "8", 8), new Card("Spades", "2", 2),
                new Card("Diamonds", "0", 0), new Card("Clubs", "8", 8));
        String result = baccaratService.playRound();
        assertEquals("Döntetlen! Mindkét fél pontszáma: 8", result);
    }

    @Test
    public void testPlayerDrawsThirdCard() {
        when(deckMock.draw()).thenReturn(new Card("Hearts", "4", 4), new Card("Spades", "1", 1),
                new Card("Diamonds", "6", 6), new Card("Clubs", "5", 5), new Card("Hearts", "2", 2));
        String result = baccaratService.playRound();
        assertTrue(result.contains("Játékos nyert! Pontszám: 7")); // Feltételezve, hogy a harmadik lap a játékos számára kedvező
    }

    @Test
    public void testBankerDrawsThirdCard() {
        when(deckMock.draw()).thenReturn(new Card("Hearts", "2", 2), new Card("Spades", "3", 3),
                new Card("Diamonds", "6", 6), new Card("Clubs", "5", 5), new Card("Spades", "5", 5), new Card("Diamonds", "1", 1));
        String result = baccaratService.playRound();
        assertTrue(result.contains("Bankár nyert! Pontszám: 7")); // Feltételezve, hogy a bankár harmadik lapja kedvező
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
        assertFalse(baccaratService.placeBet("player", 20)); // A tét 20, ami több, mint a játékos zsetonjai
    }

    @Test
    public void testWinningBetUpdatesChipsCorrectly() {
        baccaratService.getPlayer().setChips(50);
        baccaratService.placeBet("player", 10);
        // Konfiguráljuk úgy, hogy a "player" nyerjen (pl. játékos pontszáma 8, bankáré 7)
        when(deckMock.draw()).thenReturn(new Card("Hearts", "8", 8), new Card("Diamonds", "K", 0),
                new Card("Spades", "7", 7), new Card("Clubs", "Q", 0),
                new Card("Hearts", "2", 2)); // További lapok, ha szükséges
        baccaratService.playRound();
        assertEquals(70, baccaratService.getPlayer().getChips()); // Feltételezzük, hogy a nyeremény kétszerese a tétnek
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
    public void testTieReturnsBet() {
        baccaratService.getPlayer().setChips(50);
        baccaratService.placeBet("tie", 10);
        // Konfiguráljuk úgy, hogy döntetlen legyen (pl. mindkét fél pontszáma 8)
        when(deckMock.draw()).thenReturn(new Card("Hearts", "8", 8), new Card("Diamonds", "Q", 0),
                new Card("Spades", "8", 8), new Card("Clubs", "K", 0),
                new Card("Hearts", "2", 2)); // További lapok, ha szükséges
        baccaratService.playRound();
        assertEquals(50, baccaratService.getPlayer().getChips()); // A tét visszatérítésre kerül döntetlen esetén
    }

    @Test
    public void testInvalidBetType() {
        assertFalse(baccaratService.placeBet("invalid_type", 10)); // Érvénytelen fogadási típus
    }

    @Test
    public void testNegativeBetAmount() {
        assertFalse(baccaratService.placeBet("player", -10)); // Érvénytelen fogadási összeg
    }

}
