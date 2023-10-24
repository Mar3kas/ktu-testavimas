package com.projektas.controller;

import com.projektas.itprojektas.controller.AuthenticationController;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;
import com.projektas.itprojektas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class AuthenticationControllerTest {
    @Mock
    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthenticationController authenticationController = new AuthenticationController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void testLoginView() throws Exception {
        MvcResult result = mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testShowRegistrationForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"))
                .andExpect(model().attributeExists("user"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testSuccessfulUserRegistration() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("test");
        userDTO.setSurname("test");
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpassword");

        MvcResult result = mockMvc.perform(post("/register/save")
                        .flashAttr("user", userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registerPage?success"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());

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

        MvcResult result = mockMvc.perform(post("/register/save")
                        .flashAttr("user", userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"))
                .andExpect(model().attributeExists("user"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
        assertNotNull(result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.user").toString().contains("Field error"));
    }

    @Test
    void testRegisterUserWithErrors() throws Exception {
        UserDTO userDTO = new UserDTO();

        MvcResult result = mockMvc.perform(post("/register/save")
                        .flashAttr("user", userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"))
                .andExpect(model().attribute("user", userDTO))
                .andExpect(model().hasErrors())
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
        assertNotNull(result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.user").toString().contains("Field error"));
    }

    @Test
    void testUserLogoutTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }
}