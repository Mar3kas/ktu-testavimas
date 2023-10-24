package com.projektas.itprojektas.configs;

import com.projektas.itprojektas.model.Admin;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.service.impl.AdminServiceImpl;
import com.projektas.itprojektas.service.impl.ConsultantServiceImpl;
import com.projektas.itprojektas.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserServiceImpl userService;
    private final AdminServiceImpl adminService;
    private final ConsultantServiceImpl consultantService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public CustomAuthenticationProvider(
            UserServiceImpl userService,
            AdminServiceImpl adminService,
            ConsultantServiceImpl consultantService,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userService = userService;
        this.adminService = adminService;
        this.consultantService = consultantService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.findUserByUsername(username);
        Admin admin = adminService.findAdminByUsername(username);
        Consultant consultant = consultantService.findConsultantByUsername(username);

        if (isCredentialValid(user, password) || isCredentialValid(admin, password) || isCredentialValid(consultant, password)) {
            GrantedAuthority authority = new SimpleGrantedAuthority(getRoleFromPrincipal(user, admin, consultant));
            return new UsernamePasswordAuthenticationToken(username, password, Collections.singletonList(authority));
        }

        throw new BadCredentialsException("Authentication failed for " + username);
    }

    private boolean isCredentialValid(Object databaseObject, String password) {
        if (databaseObject instanceof User user) {
            return bCryptPasswordEncoder.matches(password, user.getPassword());
        } else if (databaseObject instanceof Admin admin) {
            return bCryptPasswordEncoder.matches(password, admin.getPassword());
        } else if (databaseObject instanceof Consultant consultant) {
            return bCryptPasswordEncoder.matches(password, consultant.getPassword());
        }
        return false;
    }

    private String getRoleFromPrincipal(User user, Admin admin, Consultant consultant) {
        if (Objects.nonNull(user)) {
            return user.getRole().toString();
        } else if (Objects.nonNull(admin)) {
            return admin.getRole().toString();
        } else if (Objects.nonNull(consultant)) {
            return consultant.getRole().toString();
        }
        throw new IllegalArgumentException("Invalid database object");
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
