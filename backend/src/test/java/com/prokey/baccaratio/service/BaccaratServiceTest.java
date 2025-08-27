package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Player;
import com.prokey.baccaratio.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaccaratServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private BaccaratService baccaratService;

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("testuser", 1000);
    }

    @Test
    void getOrCreatePlayer_FindsExistingPlayer() {
        when(playerRepository.findByName("testuser")).thenReturn(Optional.of(player));
        Player foundPlayer = baccaratService.getOrCreatePlayer("testuser");
        assertEquals("testuser", foundPlayer.getName());
        verify(playerRepository).findByName("testuser");
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void getOrCreatePlayer_CreatesNewPlayer() {
        when(playerRepository.findByName("newuser")).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Player newPlayer = baccaratService.getOrCreatePlayer("newuser");

        assertEquals("newuser", newPlayer.getName());
        assertEquals(1000, newPlayer.getChips());
        verify(playerRepository).findByName("newuser");
        verify(playerRepository).save(any(Player.class));
    }
}
