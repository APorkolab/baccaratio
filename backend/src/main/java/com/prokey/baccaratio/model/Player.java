package com.prokey.baccaratio.model;

public class Player {
    private String name;
    private int chips;

    public Player(String name, int chips) {
        this.name = name;
        this.chips = chips;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void win(int amount) {
        this.chips += amount;
    }

    public boolean lose(int amount) {
        if (this.chips >= amount) {
            this.chips -= amount;
            return true;
        }
        return false;
    }
}
