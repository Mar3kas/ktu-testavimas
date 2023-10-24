package com.projektas.itprojektas.service.impl;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.ConsultantStatus;
import com.projektas.itprojektas.model.dto.ConsultantDTO;
import com.projektas.itprojektas.model.Roles;
import com.projektas.itprojektas.repository.ConsultantRepository;
import com.projektas.itprojektas.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultantServiceImpl implements ConsultantService {

    private final ConsultantRepository consultantRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public ConsultantServiceImpl(ConsultantRepository consultantRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.consultantRepository = consultantRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Consultant findConsultantById(int id) {
        return consultantRepository.findConsultantById(id);
    }

    @Override
    public Consultant findConsultantByUsername(String username) {
        return consultantRepository.findByUsername(username);
    }

    @Override
    public List<Consultant> getAllConsultants() {
        return consultantRepository.findFreeConsultants();
    }

    @Override
    public Consultant saveConsultant(ConsultantDTO consultantDTO) {
        Consultant consultant = new Consultant();
        consultant.setName(consultantDTO.getName());
        consultant.setSurname(consultantDTO.getSurname());
        consultant.setUsername(consultantDTO.getUsername());
        consultant.setPassword(bCryptPasswordEncoder.encode(consultantDTO.getPassword()));
        consultant.setConsultantStatus(ConsultantStatus.FREE);
        consultant.setCredits(0.0);
        consultant.setRole(Roles.ROLE_CONSULTANT);
        return consultantRepository.save(consultant);
    }

    @Override
    public void occupyConsultant(int id) {
        consultantRepository.occupyConsultant(id);
    }

    @Override
    public void freeConsultant(int id) {
        consultantRepository.freeConsultant(id);
    }

    @Override
    public void updateConsultantCredits(Consultant consultant, double credits) {
        consultant.setCredits(consultant.getCredits() + credits);
        consultantRepository.save(consultant);
    }
}