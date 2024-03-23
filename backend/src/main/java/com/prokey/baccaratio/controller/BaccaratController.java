package com.prokey.baccaratio.controller;

import com.prokey.baccaratio.model.Card;
import com.prokey.baccaratio.service.BaccaratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/baccarat")
public class BaccaratController {

    private final BaccaratService baccaratService;

    @Autowired
    public BaccaratController(BaccaratService baccaratService) {
        this.baccaratService = baccaratService;
    }
    
    @PostMapping("/bet/{type}/{amount}")
    public ResponseEntity<?> placeBet(@PathVariable("type") String type, @PathVariable("amount") int amount) {
        // Extended with new betting types
        if (amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "The bet amount must be positive."));
        }
        if (!type.equals("player") && !type.equals("banker") && !type.equals("tie")
                && !type.equals("perfectPairOne") && !type.equals("pPair")
                && !type.equals("eitherPair") && !type.equals("bPair")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid bet type."));
        }
        boolean betPlaced = baccaratService.placeBet(type, amount);
        if (betPlaced) {
            return ResponseEntity.ok(Map.of("message", String.format("Bet placed: %s, with amount: %d", type, amount)));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to place bet. Check the available chips."));
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
        return String.format("Utolsó eredmény: %s. Fogadásod: %s. Maradék zsetonok: %d",
                baccaratService.getLastResult(),
                baccaratService.getBetType(),
                baccaratService.getChips());
    }
}
