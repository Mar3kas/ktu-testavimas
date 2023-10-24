package com.projektas.itprojektas.service.impl;

import com.projektas.itprojektas.model.Roles;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;
import com.projektas.itprojektas.repository.UserRepository;
import com.projektas.itprojektas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void updateUserCredits(User user, double credits, String flag) {
        if (flag.equals("Increase")) {
            user.setCredits(user.getCredits() + credits);
        } else if (flag.equals("Decrease")) {
            user.setCredits(user.getCredits() - credits);
        }
        userRepository.save(user);
    }

    @Override
    public User saveUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setUsername(userDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setRole(Roles.ROLE_USER);
        user.setCredits(0.0);
        return userRepository.save(user);
    }
}