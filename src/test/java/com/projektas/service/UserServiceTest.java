package com.projektas.service;

import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;
import com.projektas.itprojektas.repository.UserRepository;
import com.projektas.itprojektas.service.UserService;
import com.projektas.itprojektas.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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

    @ParameterizedTest
    @CsvSource({ "100.0, 50.0, Increase, 150.0", "100.0, 30.0, Decrease, 70.0" })
    void testUpdateUserCredits(double initialCredits, double creditsToChange, String flag, double expectedCredits) {
        User user = new User();
        user.setCredits(initialCredits);

        userService.updateUserCredits(user, creditsToChange, flag);

        verify(userRepository, times(1)).save(user);

        assertEquals(expectedCredits, user.getCredits());
    }

    @Test
    void testSaveUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John");
        userDTO.setSurname("Doe");
        userDTO.setUsername("john.doe");
        userDTO.setPassword("password123");
        userDTO.setCredits(20);

        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setUsername("john.doe");
        user.setPassword("encodedPassword");
        user.setCredits(20);

        when(bCryptPasswordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User created = userService.saveUser(userDTO);

        verify(userRepository, times(1)).save(any(User.class));
        verify(bCryptPasswordEncoder, times(1)).encode(userDTO.getPassword());

        assertEquals(userDTO.getName(), created.getName());
        assertEquals(userDTO.getUsername(), created.getUsername());
        assertEquals("encodedPassword", created.getPassword());
    }

    @Test
    void testDoNotSaveUserInvalidData() {
        UserDTO userDTO = new UserDTO();
        userDTO.setCredits(-20);

        doThrow(new DataIntegrityViolationException("Invalid data")).when(userRepository).save(any(User.class));

        assertThrows(DataIntegrityViolationException.class, () -> userService.saveUser(userDTO));
    }
}