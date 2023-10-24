package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.dto.ConsultantDTO;

import java.util.List;

public interface ConsultantService {

    Consultant findConsultantById(int id);

    Consultant findConsultantByUsername(String username);

    List<Consultant> getAllConsultants();

    Consultant saveConsultant(ConsultantDTO consultantDTO);

    void occupyConsultant(int id);

    void freeConsultant(int id);

    void updateConsultantCredits(Consultant consultant, double credits);
}
