package com.games.poker.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

public class TransactionInfo {

	private UUID id;
	private String description;
	private BigDecimal amount;
	private Direction direction;
	private boolean duesCleared;
	private Date date;
	
	public TransactionInfo() {
		
	}
	
	public TransactionInfo(UUID id, String description, BigDecimal amount, 
			Direction direction, boolean duesCleared,  Date date) {
		setId(id);
		setDescription(description);
		setAmount(amount);
		setDirection(direction);
		setDuesCleared(duesCleared);
		setDate(date);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public boolean isDuesCleared() {
		return duesCleared;
	}

	public void setDuesCleared(boolean duesCleared) {
		this.duesCleared = duesCleared;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
