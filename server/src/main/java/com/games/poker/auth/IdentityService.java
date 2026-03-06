package com.games.poker.auth;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.games.poker.dto.LoginRequest;
import com.games.poker.dto.RegisterRequest;
import com.games.poker.exceptions.AuthenticationException;
import com.games.poker.exceptions.NotFoundException;
import com.games.poker.exceptions.PokerException;
import com.games.poker.model.User;
import com.games.poker.model.enums.Role;
import com.games.poker.persistence.UserRepository;

@Service
public class IdentityService {
	
    private static final Logger logger = LogManager.getLogger(IdentityService.class);

	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
    @Autowired
    private JwtUtil jwtUtil;
    
    public UserDetails loadUser(String userName) {
    	User user = userRepository.find(userName);
    	if(user == null) {
    		throw new NotFoundException("User NotFound!!!");
    	}
    	
    	GrantedAuthority authority = null;
    	if(user.getRole() != null) {
    		authority = new SimpleGrantedAuthority(user.getRole().name());
    	}
    	
    	List<GrantedAuthority> authorities = authority != null ? List.of(authority) : new ArrayList<>();
    	
    	return new PokerUserDetails(user.getUserName(),
    			user.getPassword(),authorities);
    }
	
	public String register(RegisterRequest request) {
		
		String methodName = "IdentityService.register()";
		logger.info("{} In registering user :{}", methodName, request.getUserName());
		
		if(userRepository.exists(request.getUserName())) {
			throw new PokerException("userName already exists");
		}
		

        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setRole(Role.USER);

        user = userRepository.save(user);
		
		logger.info("{} Out registering user :{}", methodName, request.getUserName());

		return user.getUserName();
	}
	
	public String login(LoginRequest request) {
		String methodName = "IdentityService.login()";
		
		String userName = request.getUserName();

		logger.info("{} In Authenticating user :{}", methodName, userName);

		User user = userRepository.find(userName);
		if(user == null) {
			throw new NotFoundException("User : %s not found".formatted(userName));
		}
		
		String password = request.getPassword();
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new AuthenticationException("Authentication Failed");
		}
		return jwtUtil.generateToken(userName);
	}

}
