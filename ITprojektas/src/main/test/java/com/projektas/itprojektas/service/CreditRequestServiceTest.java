package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.CreditRequest;
import com.projektas.itprojektas.model.CreditRequestTestData;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.CreditRequestDTO;
import com.projektas.itprojektas.repository.CreditRequestRepository;
import com.projektas.itprojektas.service.impl.CreditRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class CreditRequestServiceTest {

    @Mock
    private CreditRequestRepository creditRequestRepository;
    private CreditRequestService creditRequestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        creditRequestService = new CreditRequestServiceImpl(creditRequestRepository);
    }

    static Stream<CreditRequestTestData> creditRequestTestData() {
        return Stream.of(
                new CreditRequestTestData(new User(), 50.5),
                new CreditRequestTestData(new User(), 75.0),
                new CreditRequestTestData(new User(), 30.5)
        );
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
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setId(1);
        creditRequest.setCredits(50.0);
        creditRequest.setUser(new User());

        int id = 1;

        doNothing().when(creditRequestRepository).deleteById(id);

        creditRequestService.deleteCreditRequest(id);

        verify(creditRequestRepository, times(1)).deleteById(id);

        assertEquals(Optional.empty(), creditRequestService.getCreditRequest(id));
    }

    @ParameterizedTest
    @MethodSource("creditRequestTestData")
    void testSaveCreditRequest(CreditRequestTestData testData) {
        CreditRequestDTO creditRequestDTO = testData.getCreditRequestDTO();
        CreditRequest creditRequest = testData.getCreditRequest();

        when(creditRequestRepository.save(any(CreditRequest.class))).thenReturn(creditRequest);

        CreditRequest actual = creditRequestService.saveCreditRequest(creditRequestDTO);

        verify(creditRequestRepository, times(1)).save(any(CreditRequest.class));

        assertNotNull(actual.getUser());
        assertTrue(actual.getCredits() > 0);
        assertEquals(actual.getUser(), creditRequest.getUser());
    }

    @Test
    void testDoNotSaveCreditRequestInvalidData() {
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setCredits(-20.0);

        doThrow(new DataIntegrityViolationException("Invalid data")).when(creditRequestRepository).save(any(CreditRequest.class));

        assertThrows(DataIntegrityViolationException.class, () -> creditRequestService.saveCreditRequest(creditRequestDTO));
    }

    @Test
    void testGetAllCreditRequestsEmptyRepository() {
        List<CreditRequest> expected = Collections.emptyList();

        when(creditRequestRepository.findAll()).thenReturn(expected);

        List<CreditRequest> actualRequests = creditRequestService.getAllCreditRequests();

        assertNotNull(actualRequests);
        assertTrue(actualRequests.isEmpty());
        assertEquals(expected, actualRequests);
    }

    @Test
    void testGetCreditRequestInvalidId() {
        Optional<CreditRequest> expected = Optional.empty();
        int invalidId = -1;

        when(creditRequestRepository.findById(invalidId)).thenReturn(expected);

        Optional<CreditRequest> actualOptional = creditRequestService.getCreditRequest(invalidId);

        assertNotNull(actualOptional);
        assertFalse(actualOptional.isPresent());
        assertEquals(expected, actualOptional);
    }
}
