package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;
import com.projektas.itprojektas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private BindingResult bindingResult;
    private MockMvc mockMvc;
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void testLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"))
                .andDo(print());
    }

    @Test
    void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"))
                .andExpect(model().attributeExists("user"))
                .andDo(print());
    }

    @Test
    void testSuccessfulUserRegistration() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("test");
        userDTO.setSurname("test");
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpassword");

        mockMvc.perform(post("/register/save")
                        .flashAttr("user", userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registerPage?success"))
                .andDo(print());

        verify(userService, times(1)).saveUser(userDTO);
    }

    @Test
    void testRegisterUserWithExistingUsernameErrorMessage() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("test");
        userDTO.setSurname("test");
        userDTO.setUsername("existinguser");
        userDTO.setPassword("testpassword");

        User existingUser = new User();
        existingUser.setUsername("existinguser");

        when(userService.findUserByUsername(userDTO.getUsername())).thenReturn(existingUser);

        mockMvc.perform(post("/register/save")
                        .flashAttr("user", userDTO)
                        .flashAttr("bindingResult", bindingResult))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"))
                .andExpect(model().attributeExists("user"))
                .andDo(print());
    }

    @Test
    void testRegisterUserWithErrors() throws Exception {
        UserDTO userDTO = new UserDTO();

        mockMvc.perform(post("/register/save")
                        .flashAttr("user", userDTO)
                        .flashAttr("bindingResult", bindingResult))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"))
                .andExpect(model().attribute("user", userDTO))
                .andExpect(model().hasErrors())
                .andDo(print());
    }

    @Test
    void testUserLogoutTest() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andDo(print());
    }
}