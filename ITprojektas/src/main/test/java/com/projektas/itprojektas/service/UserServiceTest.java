package com.projektas.itprojektas.service;

import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;
import com.projektas.itprojektas.repository.UserRepository;
import com.projektas.itprojektas.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
    }

    @Test
    void testFindUserByUsername() {
        String username = "testuser";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(expectedUser);

        User actualUser = userService.findUserByUsername(username);

        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void testFindUserByInvalidUsername() {
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(null);

        User actualUser = userService.findUserByUsername(username);

        assertNull(actualUser);
    }

    @Test
    void testUpdateUserCreditsIncrease() {
        User user = new User();
        user.setCredits(100.0);
        double creditsToAdd = 50.0;
        String flag = "Increase";

        userService.updateUserCredits(user, creditsToAdd, flag);

        assertEquals(150.0, user.getCredits());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserCreditsDecrease() {
        User user = new User();
        user.setCredits(100.0);
        double creditsToSubtract = 30.0;
        String flag = "Decrease";

        userService.updateUserCredits(user, creditsToSubtract, flag);

        assertEquals(70.0, user.getCredits());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSaveUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John");
        userDTO.setSurname("Doe");
        userDTO.setUsername("johndoe");
        userDTO.setPassword("password123");

        when(bCryptPasswordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        userService.saveUser(userDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }
}