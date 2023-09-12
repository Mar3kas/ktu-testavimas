package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;

public interface UserService {

    User findUserByUsername(String username);

    void updateUserCredits(User user, double credits, String flag);

    void saveUser(UserDTO userDTO);
}
