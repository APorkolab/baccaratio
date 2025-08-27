package com.prokey.baccaratio.controller.dto;

public class BetResponse {
    private String message;
    private int chips;

    public BetResponse(String message, int chips) {
        this.message = message;
        this.chips = chips;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }
}
