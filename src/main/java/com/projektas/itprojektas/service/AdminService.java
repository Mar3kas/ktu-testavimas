package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.Admin;

public interface AdminService {

    Admin findAdminByUsername(String username);
}
