package com.projektas.itprojektas.repository;

import com.projektas.itprojektas.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    @Query(value = "select * from admins u where u.username = ?1 LIMIT 1", nativeQuery = true)
    Admin findByUsername(String username);
}
