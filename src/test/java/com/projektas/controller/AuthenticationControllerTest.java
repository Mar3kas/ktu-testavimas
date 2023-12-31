package com.projektas.controller;

import com.projektas.itprojektas.controller.AuthenticationController;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;
import com.projektas.itprojektas.repository.UserRepository;
import com.projektas.itprojektas.service.UserService;
import com.projektas.itprojektas.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
class AuthenticationControllerTest {
    private MockMvc mockMvc;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(userService)).build();
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

        User user = new User();
        user.setName("test");
        user.setSurname("test");
        user.setUsername("testuser");
        user.setPassword("testpassword");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(bCryptPasswordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        MvcResult result = mockMvc.perform(post("/register/save")
                        .flashAttr("user", userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registerPage?success"))
                .andDo(print())
                .andReturn();

        verify(userRepository, times(1)).save(any(User.class));

        assertNotNull(result);
        assertNull(result.getResolvedException());
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

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(existingUser);

        MvcResult result = mockMvc.perform(post("/register/save")
                        .flashAttr("user", userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"))
                .andExpect(model().attributeExists("user"))
                .andDo(print())
                .andReturn();

        verify(userRepository, never()).save(any(User.class));

        assertNotNull(result);
        assertNull(result.getResolvedException());
        ModelAndView modelAndView = result.getModelAndView();
        assertNotNull(modelAndView);
        ModelMap modelMap = (ModelMap) modelAndView.getModel();
        assertNotNull(modelMap);
        Object consultantBindingResult = modelMap.get("org.springframework.validation.BindingResult.user");
        assertNotNull(consultantBindingResult);
        assertTrue(consultantBindingResult instanceof BindingResult);
        assertTrue(((BindingResult) consultantBindingResult).hasFieldErrors());
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

        verify(userRepository, never()).save(any(User.class));

        assertNotNull(result);
        assertNull(result.getResolvedException());
        ModelAndView modelAndView = result.getModelAndView();
        assertNotNull(modelAndView);
        ModelMap modelMap = (ModelMap) modelAndView.getModel();
        assertNotNull(modelMap);
        Object consultantBindingResult = modelMap.get("org.springframework.validation.BindingResult.user");
        assertNotNull(consultantBindingResult);
        assertTrue(consultantBindingResult instanceof BindingResult);
        assertTrue(((BindingResult) consultantBindingResult).hasFieldErrors());    }

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