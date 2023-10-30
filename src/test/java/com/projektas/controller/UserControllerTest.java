package com.projektas.controller;

import com.projektas.itprojektas.controller.UserController;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.model.CreditRequest;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.CreditRequestDTO;
import com.projektas.itprojektas.repository.ConsultantRepository;
import com.projektas.itprojektas.repository.ConsultationRepository;
import com.projektas.itprojektas.repository.CreditRequestRepository;
import com.projektas.itprojektas.repository.UserRepository;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.ConsultationService;
import com.projektas.itprojektas.service.CreditRequestService;
import com.projektas.itprojektas.service.UserService;
import com.projektas.itprojektas.service.impl.ConsultantServiceImpl;
import com.projektas.itprojektas.service.impl.ConsultationServiceImpl;
import com.projektas.itprojektas.service.impl.CreditRequestServiceImpl;
import com.projektas.itprojektas.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
class UserControllerTest {
    private MockMvc mockMvc;
    @Mock
    private CreditRequestRepository creditRequestRepository;
    @Mock
    private ConsultantRepository consultantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ConsultationService consultationService;
    private ConsultantService consultantService;
    private CreditRequestService creditRequestService;
    private UserService userService;

    @BeforeEach
    public void setup() {
        creditRequestService = new CreditRequestServiceImpl(creditRequestRepository);
        consultantService = new ConsultantServiceImpl(consultantRepository, bCryptPasswordEncoder);
        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
        consultationService = new ConsultationServiceImpl(consultationRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(consultationService, consultantService, creditRequestService, userService)).build();
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

        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setUser(new User());

        when(creditRequestRepository.save(any(CreditRequest.class))).thenReturn(creditRequest);

        MvcResult result = mockMvc.perform(post("/credit/request")
                        .flashAttr("creditRequestDTO", creditRequestDTO)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/credit?success"))
                .andDo(print())
                .andReturn();

        verify(creditRequestRepository).save(any(CreditRequest.class));

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

        when(userRepository.findByUsername(any())).thenReturn(user);
        when(consultationRepository.findAll()).thenReturn(new ArrayList<>());
        when(consultantRepository.findConsultantById(anyInt())).thenReturn(consultant);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?success"))
                .andDo(print())
                .andReturn();

        verify(userRepository).findByUsername(any());
        verify(consultationRepository).findAll();
        verify(consultantRepository).findConsultantById(anyInt());

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantLoadLiveChatWithInvalidUser() throws Exception {
        User user = new User();

        when(userRepository.findByUsername(any())).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("rejected"))
                .andExpect(redirectedUrl("/"))
                .andDo(print())
                .andReturn();

        verify(userRepository).findByUsername(any());
        verify(consultantRepository, never()).save(any(Consultant.class));

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantLoadLiveChatWithInsufficientCredits() throws Exception {
        User user = new User();
        user.setCredits(5);

        when(userRepository.findByUsername(any())).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("rejected"))
                .andExpect(redirectedUrl("/"))
                .andDo(print())
                .andReturn();

        verify(userRepository).findByUsername(any());
        verify(consultantRepository, never()).save(any(Consultant.class));

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantWithExistingConsultation() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        user.setCredits(20.00);
        user.setId(1);

        Consultant consultant = new Consultant();
        consultant.setName("name");
        consultant.setSurname("surname");

        List<Consultation> consultationList = new ArrayList<>();

        Consultation consultation = new Consultation();

        consultation.setUser(user);
        consultation.setConsultant(consultant);
        consultation.setFinished(false);
        consultationList.add(consultation);

        when(userRepository.findByUsername(any())).thenReturn(user);
        when(consultationRepository.findAll()).thenReturn(consultationList);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("rejected"))
                .andExpect(redirectedUrl("/"))
                .andDo(print())
                .andReturn();

        verify(userRepository).findByUsername(any());
        verify(consultationRepository).findAll();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testReserveConsultantLoadLiveChatWithInvalidConsultant() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setCredits(20.00);

        when(userRepository.findByUsername(any())).thenReturn(user);
        when(consultationRepository.findAll()).thenReturn(Collections.emptyList());
        when(consultantRepository.findConsultantById(anyInt())).thenReturn(null);

        MvcResult result = mockMvc.perform(post("/reserve/consultant/{id}", 1)
                        .principal(mock(Authentication.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("rejected"))
                .andExpect(redirectedUrl("/"))
                .andDo(print())
                .andReturn();

        verify(userRepository).findByUsername(any());
        verify(consultationRepository).findAll();
        verify(consultantRepository).findConsultantById(anyInt());

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }
}