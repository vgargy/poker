package com.games.poker.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.games.poker.model.Game;

public interface GameRepository extends CrudRepository<Game, UUID> {

	
    @Query(value = """
            select a0.* from games.game a0
            where a0.id = :id 
    """, nativeQuery = true)
	Game find(
			@Param("id") UUID id);
    
    
    @Query(value = """
            select a0.* from games.game a0
            order by date desc
    """, nativeQuery = true)
    List<Game> findAll();

}
