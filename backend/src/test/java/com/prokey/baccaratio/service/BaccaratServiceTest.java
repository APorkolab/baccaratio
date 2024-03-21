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

}
