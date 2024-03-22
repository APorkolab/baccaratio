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
        // Kibővítve az új fogadási típusokkal
        if (amount <= 0) {
            return "A tét összegének pozitívnak kell lennie.";
        }
        if (!type.equals("player") && !type.equals("banker") && !type.equals("tie")
               && !type.equals("perfectPairOne") && !type.equals("pPair")
                && !type.equals("eitherPair") && !type.equals("bPair")) {
            return "Érvénytelen fogadási típus.";
        }
        boolean betPlaced = baccaratService.placeBet(type, amount);
        if (betPlaced) {
            return String.format("Fogadás helyezve: %s, összeggel: %d", type, amount);
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
        return String.format("Utolsó eredmény: %s. Fogadásod: %s. Maradék zsetonok: %d",
                baccaratService.getLastResult(),
                baccaratService.getBetType(),
                baccaratService.getChips());
    }
}
