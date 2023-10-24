package com.projektas.itprojektas.service.impl;

import com.projektas.itprojektas.model.CreditRequest;
import com.projektas.itprojektas.model.dto.CreditRequestDTO;
import com.projektas.itprojektas.repository.CreditRequestRepository;
import com.projektas.itprojektas.service.CreditRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreditRequestServiceImpl implements CreditRequestService {

    private final CreditRequestRepository creditRequestRepository;

    @Autowired
    public CreditRequestServiceImpl(CreditRequestRepository creditRequestRepository) {
        this.creditRequestRepository = creditRequestRepository;
    }

    @Override
    public List<CreditRequest> getAllCreditRequests() {
        return creditRequestRepository.findAll();
    }

    @Override
    public Optional<CreditRequest> getCreditRequest(int id) {
        return creditRequestRepository.findById(id);
    }

    @Override
    public void deleteCreditRequest(int id) {
        creditRequestRepository.deleteById(id);
    }

    @Override
    public CreditRequest saveCreditRequest(CreditRequestDTO creditRequestDTO) {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setUser(creditRequestDTO.getUser());
        creditRequest.setCredits(creditRequestDTO.getCredits());
        return creditRequestRepository.save(creditRequest);
    }
}
