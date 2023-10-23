package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.CreditRequest;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.CreditRequestDTO;
import com.projektas.itprojektas.repository.CreditRequestRepository;
import com.projektas.itprojektas.service.impl.CreditRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditRequestServiceTest {

    @Mock
    private CreditRequestRepository creditRequestRepository;
    private CreditRequestService creditRequestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        creditRequestService = new CreditRequestServiceImpl(creditRequestRepository);
    }

    @Test
    void testGetAllCreditRequests() {
        List<CreditRequest> expectedRequests = Collections.singletonList(new CreditRequest());
        when(creditRequestRepository.findAll()).thenReturn(expectedRequests);

        List<CreditRequest> actualRequests = creditRequestService.getAllCreditRequests();

        assertNotNull(actualRequests);
        assertEquals(expectedRequests.size(), actualRequests.size());
    }

    @Test
    void testGetCreditRequest() {
        int id = 1;
        CreditRequest expectedRequest = new CreditRequest();
        Optional<CreditRequest> expectedOptional = Optional.of(expectedRequest);
        when(creditRequestRepository.findById(id)).thenReturn(expectedOptional);

        Optional<CreditRequest> actualOptional = creditRequestService.getCreditRequest(id);

        assertNotNull(actualOptional);
        assertTrue(actualOptional.isPresent());
        assertEquals(expectedRequest, actualOptional.get());
    }

    @Test
    void testDeleteCreditRequest() {
        int id = 1;

        creditRequestService.deleteCreditRequest(id);

        verify(creditRequestRepository, times(1)).deleteById(id);
    }

    @Test
    void testSaveCreditRequest() {
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setUser(new User());
        creditRequestDTO.setCredits(50.5);

        creditRequestService.saveCreditRequest(creditRequestDTO);

        verify(creditRequestRepository, times(1)).save(any(CreditRequest.class));
    }

    @Test
    void testGetAllCreditRequestsEmptyRepository() {
        when(creditRequestRepository.findAll()).thenReturn(Collections.emptyList());

        List<CreditRequest> actualRequests = creditRequestService.getAllCreditRequests();

        assertNotNull(actualRequests);
        assertTrue(actualRequests.isEmpty());
    }

    @Test
    void testGetCreditRequestInvalidId() {
        int invalidId = -1;
        when(creditRequestRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<CreditRequest> actualOptional = creditRequestService.getCreditRequest(invalidId);

        assertNotNull(actualOptional);
        assertFalse(actualOptional.isPresent());
    }
}
