package com.games.poker.model;

import static org.springframework.util.StringUtils.capitalize;
import static org.springframework.util.StringUtils.hasLength;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="game", schema="games",  uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "venue"})
}) 
public class Game {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id  ", nullable = false)
	private UUID id;
	
    @Column(name = "date", nullable = false)
	private Date date;
    
    @Column(name = "venue")
	private String venue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
    
    @Column(name = "buy_in", nullable = false)
    private BigDecimal buyIn = new BigDecimal(50.0);
    
    @OneToMany(mappedBy = "game")
    private List<PlayerGame> playerGames;
    
    @OneToMany(mappedBy = "game")
    @OrderColumn(name = "display_order")
    private List<Transaction> transactions;
    
    
    @PrePersist
    @PreUpdate
    private void normalizeName() {
    	if(hasLength(venue)) {
    		venue = capitalize(venue.toLowerCase());
    	}
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

	public List<PlayerGame> getPlayerGames() {
		return playerGames;
	}

	public void setPlayerGames(List<PlayerGame> playerGames) {
		this.playerGames = playerGames;
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

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

}
