package com.projektas.itprojektas.repository;

import com.projektas.itprojektas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM users u WHERE u.username = ?1 LIMIT 1", nativeQuery = true)
    User findByUsername(String username);
}
