package com.projektas.itprojektas.service.impl;

import com.projektas.itprojektas.configs.CustomAdminDetails;
import com.projektas.itprojektas.model.Admin;
import com.projektas.itprojektas.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AdminDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminDetailsServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username);
        if (Objects.nonNull(admin)) {
            return new CustomAdminDetails(admin);
        }
        throw new UsernameNotFoundException("Username not found");
    }
}
