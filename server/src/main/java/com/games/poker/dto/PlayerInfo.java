package com.games.poker.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PlayerInfo {
	
	private UUID id;
	private BigDecimal buyIns;
	private BigDecimal credit;
	private BigDecimal cashIn;
	private String name;
	private List<TransactionInfo> transactions;

	public PlayerInfo() {
		
	}
	
	public PlayerInfo(UUID id, String name) {
		setId(id);
		setName(name);
	}
	
	public PlayerInfo(UUID id, String name, BigDecimal buyIns, BigDecimal credit, BigDecimal cashIn) {
		this(id, name);
		setBuyIns(buyIns);
		setCredit(credit);
		setCashIn(cashIn);
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}

	public BigDecimal getBuyIns() {
		return buyIns;
	}

	public void setBuyIns(BigDecimal buyIns) {
		this.buyIns = buyIns;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TransactionInfo> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionInfo> transactions) {
		this.transactions = transactions;
	}


}
