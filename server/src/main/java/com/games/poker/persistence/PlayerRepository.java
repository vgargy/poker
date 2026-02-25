package com.games.poker.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.games.poker.model.Player;

public interface PlayerRepository extends CrudRepository<Player, UUID> {
	
    @Query(value = """
            select a0.* from games.player a0
            where a0.first_name = :firstName 
              and a0.last_name = :lastName 
    """, nativeQuery = true)
	Player find(
			@Param("firstName") String firstName,
			@Param("lastName") String lastName);
    
    @Query(value = """
            select a0.* from games.player a0
            where a0.id = :id 
    """, nativeQuery = true)
	Player find(
			@Param("id") UUID id);
    
    @Query(value = """
            select a0.* from games.player a0
            order by last_name 
    """, nativeQuery = true)
    List<Player> findAll();

}
