package com.quest.etna.repositories;

import com.quest.etna.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);


    @Query(value = "SELECT * FROM user WHERE username LIKE BINARY :username", nativeQuery = true)
    User findByUsernameCaseSensitive(String username);
}