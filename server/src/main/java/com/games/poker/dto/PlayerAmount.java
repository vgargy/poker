package com.games.poker.dto;

import java.math.BigDecimal;

import com.games.poker.model.Player;

public record PlayerAmount(Player player, BigDecimal amount) {

}
