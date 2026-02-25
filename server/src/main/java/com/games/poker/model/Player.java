package com.games.poker.model;

import static org.springframework.util.StringUtils.capitalize;
import static org.springframework.util.StringUtils.hasLength;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="player", schema="games",  uniqueConstraints = {
        @UniqueConstraint(columnNames = {"first_name", "last_name"})
})        
public class Player {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id  ", nullable = false)
	private UUID id;
	
    @Column(name = "first_name", nullable = false)
	private String firstName;

    @Column(name = "last_name", nullable = false)
	private String lastName;

    @OneToMany(mappedBy = "player")
    private List<PlayerGame> playerGames;
    
	@OneToMany(mappedBy = "payer")
	private List<Transaction> moneyOut;

	@OneToMany(mappedBy = "receiver")
	private List<Transaction> moneyIn;
    
    @PrePersist
    @PreUpdate
    private void normalizeName() {
    	if(hasLength(firstName)) {
    		firstName = capitalize(firstName.toLowerCase());
    	}

    	if(hasLength(lastName)) {
    		lastName = capitalize(lastName.toLowerCase());
    	}

    }
    
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<PlayerGame> getPlayerGames() {
		return playerGames;
	}

	public void setPlayerGames(List<PlayerGame> playerGames) {
		this.playerGames = playerGames;
	}
	
	public List<Transaction> getMoneyIn() {
		return moneyIn;
	}

	public void setMoneyIn(List<Transaction> moneyIn) {
		this.moneyIn = moneyIn;
	}

	public List<Transaction> getMoneyOut() {
		return moneyOut;
	}

	public void setMoneyOut(List<Transaction> moneyOut) {
		this.moneyOut = moneyOut;
	}
	
	@Transient
	public String getName() {
		return "%s %s".formatted(firstName, lastName);
	}

}
