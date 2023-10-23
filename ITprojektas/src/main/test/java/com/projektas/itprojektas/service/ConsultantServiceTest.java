package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.dto.ConsultantDTO;
import com.projektas.itprojektas.repository.ConsultantRepository;
import com.projektas.itprojektas.service.impl.ConsultantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class ConsultantServiceTest {

    @Mock
    private ConsultantRepository consultantRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ConsultantService consultantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consultantService = new ConsultantServiceImpl(consultantRepository, bCryptPasswordEncoder);
    }

    @Test
    void testFindConsultantById() {
        int id = 1;
        Consultant expectedConsultant = new Consultant();
        expectedConsultant.setId(id);
        when(consultantRepository.findConsultantById(id)).thenReturn(expectedConsultant);

        Consultant actualConsultant = consultantService.findConsultantById(id);

        assertEquals(expectedConsultant, actualConsultant);
    }

    @Test
    void testFindNonExistingConsultantById() {
        int id = 1;
        when(consultantRepository.findConsultantById(id)).thenReturn(null);

        Consultant actualConsultant = consultantService.findConsultantById(id);

        assertNull(actualConsultant);
    }

    @Test
    void testFindConsultantByUsername() {
        String username = "consultant123";
        Consultant expectedConsultant = new Consultant();
        expectedConsultant.setUsername(username);
        when(consultantRepository.findByUsername(username)).thenReturn(expectedConsultant);

        Consultant actualConsultant = consultantService.findConsultantByUsername(username);

        assertNotNull(actualConsultant);
        assertEquals(expectedConsultant, actualConsultant);
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
    }

    @Test
    void testSaveConsultant() {
        ConsultantDTO consultantDTO = new ConsultantDTO();
        consultantDTO.setName("John");
        consultantDTO.setSurname("Doe");
        consultantDTO.setUsername("john.doe");
        consultantDTO.setPassword("password123");

        when(bCryptPasswordEncoder.encode(consultantDTO.getPassword())).thenReturn("encodedPassword");

        consultantService.saveConsultant(consultantDTO);

        verify(consultantRepository, times(1)).save(any(Consultant.class));
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

    @Test
    void testUpdateConsultantCredits() {
        Consultant consultant = new Consultant();
        consultant.setCredits(100.0);
        double creditsToAdd = 50.0;

        consultantService.updateConsultantCredits(consultant, creditsToAdd);

        assertEquals(150.0, consultant.getCredits());
        verify(consultantRepository, times(1)).save(consultant);
    }
}
