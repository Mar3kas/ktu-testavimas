package com.projektas.controller;

import com.projektas.itprojektas.controller.UserController;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.CreditRequestDTO;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.ConsultationService;
import com.projektas.itprojektas.service.CreditRequestService;
import com.projektas.itprojektas.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class UserControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ConsultationService consultationService;
    @Mock
    private ConsultantService consultantService;
    @Mock
    private CreditRequestService creditRequestService;
    @Mock
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        UserController userController = new UserController(consultationService, consultantService, creditRequestService, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testViewCreditRequestForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/credit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("creditRequestDTO"))
                .andExpect(view().name("creditRequestPage"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testCreateCreditRequestWithValidData() throws Exception {
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setUser(new User());

        MvcResult result = mockMvc.perform(post("/credit/request")
                        .flashAttr("creditRequestDTO", creditRequestDTO)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/credit?success"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantLoadLiveChatWithValidData() throws Exception {
        User user = new User();
        user.setId(1);
        user.setCredits(20);
        Consultant consultant = new Consultant();
        consultant.setId(1);

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(consultationService.getAllConsultations()).thenReturn(new ArrayList<>());
        when(consultantService.findConsultantById(anyInt())).thenReturn(consultant);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?success"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantLoadLiveChatWithInvalidUser() throws Exception {
        User user = new User();

        when(userService.findUserByUsername(any())).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("rejected"))
                .andExpect(redirectedUrl("/"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantLoadLiveChatWithInsufficientCredits() throws Exception {
        User user = new User();
        user.setCredits(5);

        when(userService.findUserByUsername(any())).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("rejected"))
                .andExpect(redirectedUrl("/"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantWithExistingConsultation() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setCredits(20.00);
        user.setId(1);
        List<Consultation> consultationList = new ArrayList<>();
        Consultation consultation = new Consultation();
        consultation.setUser(user);
        consultation.setConsultant(new Consultant());
        consultationList.add(consultation);

        when(userService.findUserByUsername(isNull())).thenReturn(user);
        lenient().when(consultationService.getAllConsultations()).thenReturn(consultationList);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("rejected"))
                .andExpect(redirectedUrl("/"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantLoadLiveChatWithInvalidConsultant() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setCredits(20.00);

        when(userService.findUserByUsername(isNull())).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("rejected"))
                .andExpect(redirectedUrl("/"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }
}