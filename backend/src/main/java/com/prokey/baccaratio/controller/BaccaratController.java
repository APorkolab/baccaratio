package com.prokey.baccaratio.controller;

        import com.prokey.baccaratio.service.BaccaratService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/baccarat")
public class BaccaratController {

    private final BaccaratService baccaratService;

    @Autowired
    public BaccaratController(BaccaratService baccaratService) {
        this.baccaratService = baccaratService;
    }

    @GetMapping("/play")
    public String play() {
        return baccaratService.playRound();
    }
}
