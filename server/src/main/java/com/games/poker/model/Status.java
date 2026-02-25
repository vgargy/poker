package com.games.poker.model;

public enum Status {

    New("New"),
    Settled("Settled");

    private final String value;
    Status(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
