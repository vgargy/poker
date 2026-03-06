package com.games.poker.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.games.poker.auth.IdentityService;
import com.games.poker.dto.LoginRequest;
import com.games.poker.dto.RegisterRequest;
import com.games.poker.response.WebResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/poker/auth")
public class AuthController {
	
    private static final Logger logger = LogManager.getLogger(AuthController.class);

	
	@Autowired
	IdentityService identityService;
	
    @PostMapping("/register")
    public ResponseEntity<WebResponse<?>> register(@RequestBody RegisterRequest request) {
        String method = "PokerController.newGame():";
        try {
            logger.info("{} In...", method);
            String userName = identityService.register(request);
            return ResponseEntity.ok(new WebResponse<>(userName, 
            		"user : %s created successfully".formatted(userName)));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<WebResponse<?>> login(@RequestBody LoginRequest request) {
        String method = "PokerController.login():";
        try {
            logger.info("{} In...", method);
            String token = identityService.login(request);
            return ResponseEntity.ok(new WebResponse<>(token, null));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<WebResponse<?>> logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        String method = "PokerController.logout():";
        try {
            logger.info("{} In...", method);
            String userName = null;
            if (auth != null) {
            	userName = auth.getName();
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            return ResponseEntity.ok(new WebResponse<>(null,
            		"%s Logged out Successfully".formatted(userName)));
        } catch (Exception ex){
            throw ex;
        }
    }


}
