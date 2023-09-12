package com.projektas.itprojektas.service.impl;

import com.projektas.itprojektas.configs.CustomUserDetails;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (Objects.nonNull(user)) {
            return new CustomUserDetails(user);
        }
        throw new UsernameNotFoundException("Username not found");
    }
}
