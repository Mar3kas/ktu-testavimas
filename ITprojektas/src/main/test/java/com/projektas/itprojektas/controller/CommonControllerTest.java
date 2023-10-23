package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.service.ConsultantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class CommonControllerTest {

    private MockMvc mockMvc;
    @Mock
    private ConsultantService consultantService;
    private CommonController commonController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        commonController = new CommonController(consultantService);
        mockMvc = MockMvcBuilders.standaloneSetup(commonController).build();
    }

    @Test
    void testViewHomePage() throws Exception {
        Consultant consultant1 = new Consultant();
        consultant1.setName("Consultant 1");
        Consultant consultant2 = new Consultant();
        consultant2.setName("Consultant 2");
        List<Consultant> consultants = Arrays.asList(consultant1, consultant2);

        Mockito.when(consultantService.getAllConsultants()).thenReturn(consultants);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("listConsultants"))
                .andExpect(model().attribute("listConsultants", consultants))
                .andDo(print());
    }

    @Test
    void testViewHelpPage() throws Exception {
        mockMvc.perform(get("/help"))
                .andExpect(status().isOk())
                .andExpect(view().name("helpPage"))
                .andDo(print());
    }

    @Test
    void testOpenLiveChat() throws Exception {
        mockMvc.perform(get("/consultation"))
                .andExpect(status().isOk())
                .andExpect(view().name("chatPage"))
                .andDo(print());
    }
}