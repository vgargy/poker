package com.games.poker.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class PokerUserDetails implements UserDetails {
	
    private static final long serialVersionUID = 8479917630716823787L;
	private String userName;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


    public PokerUserDetails() {
    	
    }
    
    public PokerUserDetails(String userName, String password,
    		Collection<? extends GrantedAuthority> authorities) {
    	this.userName = userName;
    	this.password = password;
    	this.authorities = authorities;
    }


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

}
