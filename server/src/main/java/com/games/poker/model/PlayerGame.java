package com.games.poker.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="player_game", schema="games",  uniqueConstraints = {
		@UniqueConstraint(columnNames = {"game_id", "player_id"})
})   
public class PlayerGame {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id  ", nullable = false)
	private UUID id;

	@Column(name = "credit")
	private BigDecimal credit = new BigDecimal(0.0);

	@Column(name = "cash_in")
	private BigDecimal cashIn = new BigDecimal(0.0);

	@Column(name = "buy_ins")
	private BigDecimal buyIns = new BigDecimal(0.0);


	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="game_id", nullable  = false)
	private Game game;

	@ManyToOne
	@JoinColumn(name="player_id", nullable  = false)
	private Player player;



	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public BigDecimal getCashIn() {
		return cashIn;
	}

	public void setCashIn(BigDecimal cashIn) {
		this.cashIn = cashIn;
	}

	public BigDecimal getBuyIns() {
		return buyIns;
	}

	public void setBuyIns(BigDecimal buyIns) {
		this.buyIns = buyIns;
	}




}
