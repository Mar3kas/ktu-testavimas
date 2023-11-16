package com.projektas.service;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.dto.ConsultantDTO;
import com.projektas.itprojektas.repository.ConsultantRepository;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.impl.ConsultantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultantServiceTest {
    @Mock
    private ConsultantRepository consultantRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ConsultantService consultantService;

    @BeforeEach
    void setUp() {
        consultantService = new ConsultantServiceImpl(consultantRepository, bCryptPasswordEncoder);
    }

    @ParameterizedTest
    @CsvSource({ "1, 1", "2, 2", "3, 3" })
    void testFindConsultantById(int inputId, int expectedId) {
        Consultant expectedConsultant = new Consultant();
        expectedConsultant.setId(expectedId);

        when(consultantRepository.findConsultantById(inputId)).thenReturn(expectedConsultant);

        Consultant actualConsultant = consultantService.findConsultantById(inputId);

        assertNotNull(actualConsultant);
        assertEquals(expectedConsultant, actualConsultant);
        assertEquals(expectedId, actualConsultant.getId());
    }

    @ParameterizedTest
    @CsvSource({ "1", "2", "3" })
    void testFindNonExistingConsultantById(int inputId) {
        when(consultantRepository.findConsultantById(inputId)).thenReturn(null);

        Consultant actualConsultant = consultantService.findConsultantById(inputId);

        assertNull(actualConsultant);
    }

    @Test
    void testFindConsultantByUsername() {
        Consultant expectedConsultant = new Consultant();
        expectedConsultant.setUsername("consultant123");

        when(consultantRepository.findByUsername("consultant123")).thenReturn(expectedConsultant);

        Consultant actualConsultant = consultantService.findConsultantByUsername("consultant123");

        assertNotNull(actualConsultant);
        assertEquals(expectedConsultant, actualConsultant);
        assertEquals(expectedConsultant.getUsername(), actualConsultant.getUsername());
    }

    @Test
    void testFindConsultantByInvalidUsername() {
        String username = "nonexistent";

        when(consultantRepository.findByUsername(username)).thenReturn(null);

        Consultant actualConsultant = consultantService.findConsultantByUsername(username);

        assertNull(actualConsultant);
    }

    @Test
    void testGetAllConsultants() {
        List<Consultant> expectedConsultants = Collections.singletonList(new Consultant());

        when(consultantRepository.findFreeConsultants()).thenReturn(expectedConsultants);

        List<Consultant> actualConsultants = consultantService.getAllConsultants();

        assertEquals(expectedConsultants, actualConsultants);
        assertEquals(expectedConsultants.size(), actualConsultants.size());
    }

    @Test
    void testSaveConsultant() {
        ConsultantDTO consultantDTO = new ConsultantDTO();
        consultantDTO.setName("John");
        consultantDTO.setSurname("Doe");
        consultantDTO.setUsername("john.doe");
        consultantDTO.setPassword("password123");

        Consultant consultant = new Consultant();
        consultant.setName("John");
        consultant.setSurname("Doe");
        consultant.setUsername("john.doe");
        consultant.setPassword("encodedPassword");

        when(bCryptPasswordEncoder.encode(consultantDTO.getPassword())).thenReturn("encodedPassword");
        when(consultantRepository.save(any(Consultant.class))).thenReturn(consultant);

        Consultant actual = consultantService.saveConsultant(consultantDTO);

        verify(consultantRepository, times(1)).save(any(Consultant.class));
        verify(bCryptPasswordEncoder, times(1)).encode(consultantDTO.getPassword());

        assertEquals(consultant.getName(), actual.getName());
        assertEquals(consultant.getUsername(), actual.getUsername());
        assertEquals("encodedPassword", actual.getPassword());
    }

    @Test
    void testDoNotSaveConsultantInvalidData() {
        ConsultantDTO consultantDTO = new ConsultantDTO();
        consultantDTO.setSurname("Doe");

        doThrow(new DataIntegrityViolationException("Invalid data")).when(consultantRepository).save(any(Consultant.class));

        assertThrows(DataIntegrityViolationException.class, () -> consultantService.saveConsultant(consultantDTO));
    }

    @Test
    void testOccupyConsultant() {
        int id = 1;

        consultantService.occupyConsultant(id);

        verify(consultantRepository, times(1)).occupyConsultant(id);
    }

    @Test
    void testFreeConsultant() {
        int id = 1;

        consultantService.freeConsultant(id);

        verify(consultantRepository, times(1)).freeConsultant(id);
    }

    @ParameterizedTest
    @CsvSource({ "100.0, 50.0, 150.0", "200.0, 30.0, 230.0" })
    void testUpdateConsultantCredits(double initialCredits, double creditsToAdd, double expectedCredits) {
        Consultant consultant = new Consultant();
        consultant.setCredits(initialCredits);

        consultantService.updateConsultantCredits(consultant, creditsToAdd);

        verify(consultantRepository, times(1)).save(consultant);

        assertEquals(expectedCredits, consultant.getCredits());
    }
}
