package com.projektas.controller;

import com.projektas.itprojektas.controller.CommonController;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.repository.ConsultantRepository;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.impl.ConsultantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class CommonControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ConsultantRepository consultantRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ConsultantService consultantService;

    @BeforeEach
    public void setup() {
        consultantService = new ConsultantServiceImpl(consultantRepository, bCryptPasswordEncoder);
        mockMvc = MockMvcBuilders.standaloneSetup(new CommonController(consultantService)).build();
    }

    @Test
    void testViewHomePage() throws Exception {
        Consultant consultant1 = new Consultant();
        consultant1.setName("Consultant 1");

        Consultant consultant2 = new Consultant();
        consultant2.setName("Consultant 2");

        List<Consultant> consultants = Arrays.asList(consultant1, consultant2);

        when(consultantRepository.findFreeConsultants()).thenReturn(consultants);

        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("listConsultants"))
                .andExpect(model().attribute("listConsultants", consultants))
                .andDo(print())
                .andReturn();

        verify(consultantRepository).findFreeConsultants();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testViewHelpPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/help"))
                .andExpect(status().isOk())
                .andExpect(view().name("helpPage"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testOpenLiveChat() throws Exception {
        MvcResult result = mockMvc.perform(get("/consultation"))
                .andExpect(status().isOk())
                .andExpect(view().name("chatPage"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }
}