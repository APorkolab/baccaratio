package com.prokey.baccaratio.service;

import com.prokey.baccaratio.model.Game;
import com.prokey.baccaratio.model.Player;
import com.prokey.baccaratio.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BaccaratService {

    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private final PlayerRepository playerRepository;

    public BaccaratService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player getOrCreatePlayer(String username) {
        return playerRepository.findByName(username)
                .orElseGet(() -> {
                    Player newPlayer = new Player(username, 1000);
                    return playerRepository.save(newPlayer);
                });
    }

    @Transactional
    public Game getGameForSession(String sessionId, String username) {
        Player player = getOrCreatePlayer(username);
        // Persist any changes to player (like creating it) before starting a game
        playerRepository.save(player);
        return activeGames.computeIfAbsent(sessionId, id -> new Game(player));
    }

    @Transactional
    public void savePlayerState(Player player) {
        playerRepository.save(player);
    }

    public void endGameForSession(String sessionId) {
        activeGames.remove(sessionId);
    }

    public boolean isValidBetType(String type) {
        try {
            BetType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public enum BetType {
        PLAYER,
        BANKER,
        TIE
    }
}
