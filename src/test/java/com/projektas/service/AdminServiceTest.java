package com.projektas.service;

import com.projektas.itprojektas.model.Admin;
import com.projektas.itprojektas.repository.AdminRepository;
import com.projektas.itprojektas.service.AdminService;
import com.projektas.itprojektas.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private AdminRepository adminRepository;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(adminRepository);
    }

    @Test
    void testFindAdminByUsername() {
        String username = "admin123";

        Admin expectedAdmin = new Admin();
        expectedAdmin.setUsername(username);

        when(adminRepository.findByUsername(username)).thenReturn(expectedAdmin);

        Admin actualAdmin = adminService.findAdminByUsername(username);

        assertNotNull(actualAdmin);
        assertEquals(expectedAdmin, actualAdmin);
    }

    @Test
    void testFindAdminByInvalidUsername() {
        String username = "nonexistent";

        when(adminRepository.findByUsername(username)).thenReturn(null);

        Admin actualAdmin = adminService.findAdminByUsername(username);

        assertNull(actualAdmin);
    }
}