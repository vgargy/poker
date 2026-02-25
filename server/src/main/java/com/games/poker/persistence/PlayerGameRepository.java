package com.games.poker.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.games.poker.model.PlayerGame;

import jakarta.transaction.Transactional;

public interface PlayerGameRepository extends CrudRepository<PlayerGame, UUID> {

    @Query(value = """
            select case when count(a0.id) > 0 then true else false end from games.player_game a0
            where a0.game_id = :gameId 
            and a0.player_id = :playerId
            """, nativeQuery = true)
    boolean exists(
    		@Param("gameId") UUID gameId,
    		@Param("playerId") UUID playerId);
    
    @Transactional
    @Modifying
    @Query(value = """
    		update games.player_game
    		set buy_ins = buy_ins + :buyIns
    		where player_id = :playerId
    		and game_id = :gameId
    		""", nativeQuery = true)
    public void addRebuy(
    		@Param("gameId") UUID gameId,
    		@Param("playerId") UUID playerId,
    		@Param("buyIns") BigDecimal buyIns);
    
    @Query(value = """
            select a0.* from games.player_game a0
            where a0.game_id = :gameId 
            """, nativeQuery = true)
    public List<PlayerGame> getGameDetails(@Param("gameId") UUID gameId);
    
    @Transactional
    @Modifying
    @Query(value = """
    		update games.player_game
    		set credit = :credit
    		where player_id = :playerId
    		and game_id = :gameId
    		""", nativeQuery = true)
    public void setCredit(
    		@Param("gameId") UUID gameId,
    		@Param("playerId") UUID playerId,
    		@Param("credit") BigDecimal credit);

    @Transactional
    @Modifying
    @Query(value = """
    		update games.player_game
    		set cash_in = :cashIn
    		where player_id = :playerId
    		and game_id = :gameId
    		""", nativeQuery = true)
    public void setCashIn(
    		@Param("gameId") UUID gameId,
    		@Param("playerId") UUID playerId,
    		@Param("cashIn") BigDecimal cashIn);
    
    
    @Query(value = """
		    select case when count(a0.id) > 0 then true else false end from games.player_game a0
		    where a0.game_id = :gameId
		    and a0.credit <> 0;
            """, nativeQuery = true)
    boolean existsCredit(
    		@Param("gameId") UUID gameId);

    
    
}
