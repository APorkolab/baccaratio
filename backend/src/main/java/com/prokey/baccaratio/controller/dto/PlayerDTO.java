package com.prokey.baccaratio.controller.dto;

public class PlayerDTO {
    private String name;
    private int chips;

    public PlayerDTO(String name, int chips) {
        this.name = name;
        this.chips = chips;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }
}
