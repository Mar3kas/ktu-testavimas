package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.model.User;

import java.util.List;

public interface ConsultationService {

    Consultation saveConsultation(User user, Consultant consultant);

    void updateConsultation(Consultation consultation);

    List<Consultation> getAllConsultations();
}
