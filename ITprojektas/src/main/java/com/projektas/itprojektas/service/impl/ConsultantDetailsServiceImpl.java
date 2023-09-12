package com.projektas.itprojektas.service.impl;

import com.projektas.itprojektas.configs.CustomConsultantDetails;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.repository.ConsultantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ConsultantDetailsServiceImpl implements UserDetailsService {

    private final ConsultantRepository consultantRepository;

    @Autowired
    public ConsultantDetailsServiceImpl(ConsultantRepository consultantRepository) {
        this.consultantRepository = consultantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Consultant consultant = consultantRepository.findByUsername(username);
        if (Objects.nonNull(consultant)) {
            return new CustomConsultantDetails(consultant);
        }
        throw new UsernameNotFoundException("Username not found");
    }
}
