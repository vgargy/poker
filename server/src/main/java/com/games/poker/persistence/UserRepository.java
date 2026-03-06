package com.games.poker.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.games.poker.model.User;

public interface UserRepository extends CrudRepository<User, String> {
	
	
    @Query(value = """
            select a0.* from games.user a0
            where a0.user_name = :userName
    """, nativeQuery = true)
	User find(
			@Param("userName") String userName);
    

    @Query(value = """
            select case when count(a0.user_name) > 0 then true else false end from games.user a0
            where a0.user_name = :userName
            """, nativeQuery = true)
    boolean exists(
    		@Param("userName") String userName);

}
