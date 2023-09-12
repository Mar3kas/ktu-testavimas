package com.projektas.itprojektas.service.impl;

import com.projektas.itprojektas.model.Admin;
import com.projektas.itprojektas.repository.AdminRepository;
import com.projektas.itprojektas.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin findAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
}
