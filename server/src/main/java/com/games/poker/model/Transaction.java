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
@Table(name="transaction", schema="games",  uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payer_id", "receiver_id", "game_id"})
})  
public class Transaction {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id  ", nullable = false)
	private UUID id;
	
    @Column(name = "amount", nullable = false)
    private BigDecimal amount = new BigDecimal(0.0);
    
    @Column(name = "dues_cleared")
    private boolean duesCleared;
    
    @Column(name = "display_order")
    private int displayOrder;
	
	@ManyToOne
    @JoinColumn(name="payer_id", nullable  = false)
    private Player payer;
	
	@ManyToOne
    @JoinColumn(name="receiver_id", nullable  = false)
    private Player receiver;
	
	@ManyToOne
    @JoinColumn(name="game_id", nullable  = false)
    private Game game;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public boolean isDuesCleared() {
		return duesCleared;
	}

	public void setDuesCleared(boolean duesCleared) {
		this.duesCleared = duesCleared;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPayer() {
		return payer;
	}

	public void setPayer(Player payer) {
		this.payer = payer;
	}

	public Player getReceiver() {
		return receiver;
	}

	public void setReceiver(Player receiver) {
		this.receiver = receiver;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	

}
