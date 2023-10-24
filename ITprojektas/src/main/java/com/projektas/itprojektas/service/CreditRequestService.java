package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.CreditRequest;
import com.projektas.itprojektas.model.dto.CreditRequestDTO;

import java.util.List;
import java.util.Optional;

public interface CreditRequestService {
    List<CreditRequest> getAllCreditRequests();

    Optional<CreditRequest> getCreditRequest(int id);

    void deleteCreditRequest(int id);

    CreditRequest saveCreditRequest(CreditRequestDTO creditRequestDTO);
}
