package com.projektas.itprojektas.repository;

import com.projektas.itprojektas.model.CreditRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditRequestRepository extends JpaRepository<CreditRequest, Integer> {
}
