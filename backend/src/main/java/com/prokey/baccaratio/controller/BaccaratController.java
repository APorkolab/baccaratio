package com.prokey.baccaratio.controller;

import com.prokey.baccaratio.controller.dto.BetResponse;
import com.prokey.baccaratio.controller.dto.GameResponse;
import com.prokey.baccaratio.model.Game;
import com.prokey.baccaratio.model.Player;
import com.prokey.baccaratio.service.BaccaratService;
import com.prokey.baccaratio.service.BaccaratService.BetType;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/baccarat")
public class BaccaratController {
    private final BaccaratService baccaratService;

    public BaccaratController(BaccaratService baccaratService) {
        this.baccaratService = baccaratService;
    }

    private Game getGame(HttpSession session, Principal principal) {
        String username = principal.getName();
        return baccaratService.getGameForSession(session.getId(), username);
    }

    @PostMapping("/bet/{type}/{amount}")
    public ResponseEntity<BetResponse> placeBet(@PathVariable String type, @PathVariable int amount, HttpSession session, Principal principal) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Bet amount must be positive.");
        }
        if (!baccaratService.isValidBetType(type)) {
            throw new IllegalArgumentException("Invalid bet type.");
        }

        Game game = getGame(session, principal);
        Player player = game.getPlayer();

        if (player.getChips() < amount) {
            throw new IllegalArgumentException("Insufficient chips for this bet.");
        }

        BetType betType = BetType.valueOf(type.toUpperCase());
        game.placeBet(betType, amount);

        String message = String.format("Bet placed on %s with amount %d", type, amount);
        BetResponse response = new BetResponse(message, player.getChips());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/play")
    public ResponseEntity<GameResponse> play(HttpSession session, Principal principal) {
        Game game = getGame(session, principal);
        String result = game.playRound();

        baccaratService.savePlayerState(game.getPlayer());

        GameResponse response = new GameResponse(
            result,
            game.getPlayerCards(),
            game.getBankerCards(),
            game.getPlayer().getChips(),
            "Round played successfully."
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/state")
    public ResponseEntity<GameResponse> getGameState(HttpSession session, Principal principal) {
        Game game = getGame(session, principal);

        GameResponse response = new GameResponse(
            game.getLastResult(),
            game.getPlayerCards(),
            game.getBankerCards(),
            game.getPlayer().getChips(),
            "Current game state."
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<GameResponse> resetGame(HttpSession session, Principal principal) {
        Game game = getGame(session, principal);
        Player player = game.getPlayer();
        player.setChips(1000); // Reset chips to default
        baccaratService.savePlayerState(player);

        baccaratService.endGameForSession(session.getId());
        Game newGame = getGame(session, principal);

        GameResponse response = new GameResponse(
            "Game reset",
            newGame.getPlayerCards(),
            newGame.getBankerCards(),
            newGame.getPlayer().getChips(),
            "Game has been reset."
        );

        return ResponseEntity.ok(response);
    }
}
