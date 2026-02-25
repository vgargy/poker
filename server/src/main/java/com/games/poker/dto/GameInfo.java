package com.games.poker.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import com.games.poker.model.Status;

public class GameInfo {
	
	private UUID id;
	private Date date;
	private String venue;
	private Status status;
	private BigDecimal buyIn;
	private List<PlayerInfo> players;
	
	public GameInfo() {
		
	}
	
	public GameInfo(UUID id, Date date, String venue, Status status) {
		setId(id);
		setDate(date);
		setVenue(venue);
		setStatus(status);
	}
	
	public GameInfo(UUID id, Date date, String venue,Status status,  BigDecimal stakes, List<PlayerInfo> players) {
		this(id, date, venue, status);
		setBuyIn(stakes);
		setPlayers(players);
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}

	public List<PlayerInfo> getPlayers() {
		return players;
	}
	public void setPlayers(List<PlayerInfo> players) {
		this.players = players;
	}

	public BigDecimal getBuyIn() {
		return buyIn;
	}

	public void setBuyIn(BigDecimal buyIn) {
		this.buyIn = buyIn;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	

}
