package com.games.poker.model;

import com.games.poker.model.enums.Role;
import com.games.poker.model.util.HashStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="user", schema="games") 
public class User {
	
	@Id
    @Column(name = "user_name", nullable = false)
	private String userName;
	
	@Convert(converter = HashStringConverter.class)
    @Column(name = "password", nullable = false)
	private String password;
	
    @Column(name = "role", nullable = false)
	private Role role;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
