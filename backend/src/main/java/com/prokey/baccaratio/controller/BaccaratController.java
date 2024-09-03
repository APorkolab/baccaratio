package com.prokey.baccaratio.controller;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.model.Player;
import com.prokey.baccaratio.service.BaccaratService;
import com.prokey.baccaratio.service.BaccaratService.BetType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BaccaratController {
    private final BaccaratService baccaratService;

    public BaccaratController(BaccaratService baccaratService) {
        this.baccaratService = baccaratService;
    }

    @PostMapping("/bet/{type}/{amount}")
    public ResponseEntity<?> placeBet(@PathVariable("type") String type, @PathVariable("amount") int amount) {
        if (amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "The bet amount must be positive."));
        }
        if (!baccaratService.isValidType(type)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid bet type."));
        }
        if (baccaratService.getPlayer().getChips() < amount) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nincs elég zsetonod ehhez a fogadáshoz."));
        }
        try {
            BetType betType = BaccaratService.BetType.valueOf(type.toUpperCase());
            boolean betPlaced = baccaratService.placeBet(betType, amount);
            if (betPlaced) {
                return ResponseEntity
                        .ok(Map.of("message", String.format("Fogadás megtörtént: %s, összeg: %d", type, amount)));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Failed to place bet. Check the available chips."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid bet type."));
        }

    }

    @GetMapping("/play")
    public String play() {
        return baccaratService.playRound();
    }

    @GetMapping("/cards")
    public Map<String, List<Card>> getCards() {
        Map<String, List<Card>> cards = new HashMap<>();
        cards.put("playerCards", baccaratService.getPlayerCards());
        cards.put("bankerCards", baccaratService.getBankerCards());
        return cards;
    }

    @GetMapping("/result")
    public String getLastResult() {
        return String.format("Last result: %s. Your bet: %s. Remaining chips: %d",
                baccaratService.getLastResult(),
                baccaratService.getBetType(),
                baccaratService.getChips());
    }

    @GetMapping("/player")
    public ResponseEntity<?> getPlayer() {
        Player player = baccaratService.getPlayer();
        return player != null ? ResponseEntity.ok(player)
                : ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
    }

    @GetMapping("/player/chips")
    public ResponseEntity<?> getPlayerChips() {
        Player player = baccaratService.getPlayer();
        return player != null ? ResponseEntity.ok(Map.of("chips", player.getChips()))
                : ResponseEntity.badRequest().body(Map.of("message", "Player not found."));
    }

    @PostMapping("/player/chips")
    public ResponseEntity<?> updateChips(@RequestBody Map<String, Integer> chipsUpdate) {
        int amount = chipsUpdate.getOrDefault("amount", 0);
        boolean updated = baccaratService.updateChips(amount);
        return updated ? ResponseEntity.ok(Map.of("message", "Chips updated successfully."))
                : ResponseEntity.badRequest().body(Map.of("message", "Failed to update chips."));
    }

    @GetMapping("/player/name")
    public ResponseEntity<?> getPlayerName() {
        Player player = baccaratService.getPlayer();
        return player != null && player.getName() != null
                ? ResponseEntity.ok(Map.of("name", player.getName()))
                : ResponseEntity.badRequest().body(Map.of("message", "Player name is not set."));
    }

    @PutMapping("/player/name")
    public ResponseEntity<?> setPlayerName(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        Player player = baccaratService.getPlayer();
        if (player != null && name != null && !name.trim().isEmpty()) {
            player.setName(name);
            return ResponseEntity.ok(Map.of("message", "Player name updated successfully."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid name or player not found."));
        }
    }

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "Welcome to the Baccarat Game API!");
    }
}
