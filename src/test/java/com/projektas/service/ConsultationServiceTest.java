package com.projektas.service;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.repository.ConsultationRepository;
import com.projektas.itprojektas.service.ConsultationService;
import com.projektas.itprojektas.service.impl.ConsultationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {
    @Mock
    private ConsultationRepository consultationRepository;
    private ConsultationService consultationService;

    @BeforeEach
    public void setup() {
        consultationService = new ConsultationServiceImpl(consultationRepository);
    }

    @Test
    void testSaveConsultation() {
        User user = new User();
        Consultant consultant = new Consultant();

        Consultation savedConsultation = new Consultation();
        savedConsultation.setUser(user);
        savedConsultation.setConsultant(consultant);
        savedConsultation.setFinished(false);

        when(consultationRepository.save(any(Consultation.class))).thenReturn(savedConsultation);

        Consultation actual = consultationService.saveConsultation(user, consultant);

        verify(consultationRepository, times(1)).save(any(Consultation.class));

        assertNotNull(actual);
        assertEquals(savedConsultation.getUser(), actual.getUser());
        assertEquals(savedConsultation.getConsultant(), actual.getConsultant());
        assertFalse(actual.isFinished());
    }

    @Test
    void testDoNotSaveConsultation() {
        User user = new User();
        user.setCredits(-50);

        doThrow(new DataIntegrityViolationException("Invalid data")).when(consultationRepository).save(any(Consultation.class));

        assertThrows(DataIntegrityViolationException.class, () -> consultationService.saveConsultation(user, null));
    }

    @Test
    void testUpdateConsultation() {
        Consultation consultation = new Consultation();
        consultation.setFinished(false);

        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        consultationService.updateConsultation(consultation);

        verify(consultationRepository, times(1)).save(consultation);

        assertTrue(consultation.isFinished());
    }

    @Test
    void testGetAllConsultations() {
        List<Consultation> expectedConsultations = Collections.singletonList(new Consultation());

        when(consultationRepository.findAll()).thenReturn(expectedConsultations);

        List<Consultation> actualConsultations = consultationService.getAllConsultations();

        assertNotNull(actualConsultations);
        assertEquals(expectedConsultations.size(), actualConsultations.size());
    }

    @Test
    void testGetAllConsultationsEmptyRepository() {
        List<Consultation> actualConsultations = consultationService.getAllConsultations();

        assertNotNull(actualConsultations);
        assertTrue(actualConsultations.isEmpty());
        assertEquals(0, actualConsultations.size());
    }
}