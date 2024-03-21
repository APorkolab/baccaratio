package com.prokey.baccaratio.controller;

import com.prokey.baccaratio.service.BaccaratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/baccarat")
public class BaccaratController {

    private final BaccaratService baccaratService;

    @Autowired
    public BaccaratController(BaccaratService baccaratService) {
        this.baccaratService = baccaratService;
    }

    @PostMapping("/bet/{type}/{amount}")
    public String placeBet(@PathVariable String type, @PathVariable int amount) {
        if (amount <= 0) {
            return "A tét összegének pozitívnak kell lennie.";
        }
        if (!type.equals("player") && !type.equals("banker") && !type.equals("tie")) {
            return "Érvénytelen fogadási típus. Lehetséges értékek: 'player', 'banker', 'tie'.";
        }
        boolean betPlaced = baccaratService.placeBet(type, amount);
        if (betPlaced) {
            return "Fogadás helyezve a következőre: " + type + " összeggel: " + amount;
        } else {
            return "Nem sikerült a fogadást elhelyezni. Ellenőrizd a rendelkezésre álló zsetonok számát.";
        }
    }

    @GetMapping("/play")
    public String play() {
        return baccaratService.playRound();
    }

    @GetMapping("/result")
    public String getLastResult() {
        return "Utolsó eredmény: " + baccaratService.getLastResult() +
                ". Fogadásod: " + baccaratService.getBetType() +
                ". Maradék zsetonok: " + baccaratService.getChips();
    }
}