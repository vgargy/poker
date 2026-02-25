package com.games.poker.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.games.poker.response.WebResponse;
import com.games.poker.service.PokerService;

@RestController
@RequestMapping("/poker")
public class PokerController {
	
    private static final Logger logger = LogManager.getLogger(PokerController.class);
    
    @Autowired
    PokerService service;

    @PostMapping("/game")
    public ResponseEntity<WebResponse<?>> newGame( @RequestParam(name = "buyIn", required = false, defaultValue="50.0") BigDecimal buyIn,
    		@RequestParam(name = "venue", required = false, defaultValue="default") String venue,
    		@RequestParam(name = "date", required = false) String date) throws Exception {
        String method = "PokerController.newGame():";
        try {
            logger.info("{} In... {} ", method);
            return ResponseEntity.ok(new WebResponse<>(service.newGame(buyIn, venue, date), 
            		"Game {%s} created successfully".formatted(venue)));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    @GetMapping("/games")
    public ResponseEntity<WebResponse<?>> listGames() throws Exception {
        String method = "PokerController.listGames():";
        try {
            logger.info("{} In... {} ", method);
            return ResponseEntity.ok(new WebResponse<>(service.allGames(), null));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    @GetMapping("/game/{gameId}")
    public ResponseEntity<WebResponse<?>> getGame(@PathVariable("gameId") UUID gameId)
    		throws Exception {
        String method = "PokerController.getGame():";
        try {
            logger.info("{} In... {} ", method);
            return ResponseEntity.ok(new WebResponse<>(service.getGameDetails(gameId), null));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    @GetMapping("/player/{playerId}")
    public ResponseEntity<WebResponse<?>> getPlayer(@PathVariable("playerId") UUID playerId)
    		throws Exception {
        String method = "PokerController.getPlayer():";
        try {
            logger.info("{} In... {} ", method);
            return ResponseEntity.ok(new WebResponse<>(service.getPlayerDetails(playerId), null));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    
    @GetMapping("/players")
    public ResponseEntity<WebResponse<?>> listPlayers() throws Exception {
        String method = "PokerController.listPlayers():";
        try {
            logger.info("{} In... {} ", method);
            return ResponseEntity.ok(new WebResponse<>(service.allPlayers(), null));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    
    @PostMapping("/{gameId}/player/{firstName}/{lastName}/add")
    public ResponseEntity<WebResponse<?>> addPlayerByName( 
    		@PathVariable("gameId") UUID gameId,
    		@PathVariable("firstName") String firstName,
    		@PathVariable("lastName") String lastName) throws Exception {
        String method = "PokerController.addPlayerByName():";
        try {
            logger.info("{} In... {} ", method);
            return ResponseEntity.ok(new WebResponse<>(service.addNewPlayer(firstName, lastName, gameId),
            		"Player %s added successfully".formatted(firstName)));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    
    @PostMapping("/{gameId}/player/{playerId}/add")
    public ResponseEntity<WebResponse<?>> addPlayerById( 
    		@PathVariable("gameId") UUID gameId,
    		@PathVariable("playerId") UUID playerId) throws Exception {
        String method = "PokerController.addPlayerById():";
        try {
            logger.info("{} In... {} ", method);
            service.addPlayer(gameId, playerId);
            return ResponseEntity.ok(new WebResponse<>(service.addPlayer(gameId, playerId),
            		"Player added successfully"));
        } catch (Exception ex){
            throw ex;
        }
    }

    @PostMapping("/{gameId}/player/{playerId}/reBuy/{amount}")
    public ResponseEntity<WebResponse<?>> reBuy(
    		@PathVariable("gameId") UUID gameId,
    		@PathVariable("playerId") UUID playerId,
    		@PathVariable(name = "amount") BigDecimal amount) throws Exception {
        String method = "PokerController.reBuy():";
        try {
            logger.info("{} In... {} ", method);
            service.reBuy(gameId, playerId, amount);
            return ResponseEntity.ok(new WebResponse<>(null, "rebuy added"));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    @PostMapping("/{gameId}/player/{playerId}/credit/{amount}")
    public ResponseEntity<WebResponse<?>> credit(
    		@PathVariable("gameId") UUID gameId,
    		@PathVariable("playerId") UUID playerId,
    		@PathVariable(name = "amount") BigDecimal amount) throws Exception {
        String method = "PokerController.credit():";
        try {
            logger.info("{} In... {} ", method);
            service.credit(gameId, playerId, amount);
            return ResponseEntity.ok(new WebResponse<>(null, "amount credited"));
        } catch (Exception ex){
            throw ex;
        }
    }

    
    @PostMapping("/{gameId}/player/{playerId}/cashIn/{amount}")
    public ResponseEntity<WebResponse<?>> cashIn(
    		@PathVariable("gameId") UUID gameId,
    		@PathVariable("playerId") UUID playerId,
    		@PathVariable(name = "amount") BigDecimal amount) throws Exception {
        String method = "PokerController.cashIn():";
        try {
            logger.info("{} In... {} ", method);
            service.cashIn(gameId, playerId, amount);
            return ResponseEntity.ok(new WebResponse<>(null,null));
        } catch (Exception ex){
            throw ex;
        }
    }

    @GetMapping(value = "/{gameId}/generate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> generate(
    		@PathVariable("gameId") UUID gameId) throws Exception {
        String method = "PokerController.generate():";
        try {
            logger.info("{} In... {} ", method);
            File file = service.generate(gameId);
            StringBuilder sb = new StringBuilder("attachment; filename=\"")
                    .append(file.getName()).append("\"");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", sb.toString())
                    .body(new FileSystemResource(file));
        } catch (Exception ex){
            throw ex;
        }
    }
    
    @PostMapping(value = "/{gameId}/settle")
    public ResponseEntity<?> settle (
    		@PathVariable("gameId") UUID gameId) throws Exception {
        String method = "PokerController.settle():";
        try {
            logger.info("{} In... {} ", method);
            service.settle(gameId);

            return ResponseEntity.ok(gameId);
        } catch (Exception ex){
            throw ex;
        }
    }

}
