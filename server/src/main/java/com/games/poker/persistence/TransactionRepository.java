package com.games.poker.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.games.poker.model.Transaction;

import jakarta.persistence.Tuple;

@Transactional(readOnly = true)
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {

	@Query(value = """
			select case when count(a0.id) > 0 then true else false end from games.transaction a0
			where a0.payer_id = :payerId 
			and a0.receiver_id = :receiverId
			and a0.game_id = :gameId
			""", nativeQuery = true)
	public boolean exists(
			@Param("payerId") UUID payerId,
			@Param("receiverId") UUID receiverId,
			@Param("gameId") UUID gameId);
	
    @Transactional
    @Modifying
    @Query(value = "delete from games.transaction where game_id = :gameId", nativeQuery = true)
    public void deleteTransactions(@Param("gameId") UUID gameId);	
    

	@Query(value = """
			SELECT
			    a0.game_id,
			    CASE
			        WHEN a0.receiver_id = :playerId
			            THEN 'Credit'
			        ELSE 'Debit'
			    END AS direction,
			    SUM(a0.amount) AS amount
			FROM games.transaction a0
			WHERE :playerId
			      IN (a0.receiver_id, a0.payer_id)
			GROUP BY
			    a0.game_id,
			    direction;
			""", nativeQuery = true)
    public  List<Tuple> getTransactions(@Param("playerId") UUID playerId); 
	
}
