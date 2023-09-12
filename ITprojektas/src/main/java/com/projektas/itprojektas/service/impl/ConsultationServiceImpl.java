package com.projektas.itprojektas.service.impl;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.repository.ConsultationRepository;
import com.projektas.itprojektas.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;

    @Autowired
    public ConsultationServiceImpl(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    @Override
    public void saveConsultation(User user, Consultant consultant) {
        Consultation consultation = new Consultation();
        consultation.setUser(user);
        consultation.setFinished(false);
        consultation.setConsultant(consultant);
        consultationRepository.save(consultation);
    }

    @Override
    public void updateConsultation(Consultation consultation) {
        consultation.setFinished(true);
        consultationRepository.save(consultation);
    }

    @Override
    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }
}
