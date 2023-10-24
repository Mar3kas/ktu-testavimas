package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.CreditRequest;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.ConsultantDTO;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.CreditRequestService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class AdminControllerTest {
    private MockMvc mockMvc;
    @Mock
    private CreditRequestService creditRequestService;
    @Mock
    private ConsultantService consultantService;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        AdminController adminController = new AdminController(creditRequestService, consultantService, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void testViewAllUsersWithCreditRequests() throws Exception {
        List<CreditRequest> creditRequestList = new ArrayList<>();

        when(creditRequestService.getAllCreditRequests()).thenReturn(creditRequestList);

        MvcResult result = mockMvc.perform(get("/admin/credit/requests"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("requests", creditRequestList))
                .andExpect(view().name("adminCreditRequestsPage"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());

        verify(creditRequestService).getAllCreditRequests();
    }

    @Test
    void testRejectUserCreditRequest() throws Exception {
        int requestId = 1;
        User user = new User();
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setUser(user);

        when(creditRequestService.getCreditRequest(requestId)).thenReturn(Optional.of(creditRequest));

        MvcResult result = mockMvc.perform(delete("/admin/credit/requests/reject/{id}", requestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/credit/requests"))
                .andExpect(flash().attribute("rejected", String.format("Credit request for %s was rejected", user.getUsername())))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());

        verify(creditRequestService).getCreditRequest(requestId);
        verify(creditRequestService).deleteCreditRequest(requestId);
    }

    @Test
    void testRejectUserCreditRequestInvalidRequest() throws Exception {
        int requestId = 1;

        when(creditRequestService.getCreditRequest(requestId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(delete("/admin/credit/requests/reject/{id}", requestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/credit/requests?error"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());

        verify(creditRequestService).getCreditRequest(requestId);
        verify(creditRequestService, never()).deleteCreditRequest(requestId);
    }

    @Test
    void testApproveUserCreditRequest() throws Exception {
        int requestId = 1;
        User user = new User();
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setUser(user);

        when(creditRequestService.getCreditRequest(requestId)).thenReturn(Optional.of(creditRequest));

        MvcResult result = mockMvc.perform(post("/admin/credit/requests/approve/{id}", requestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/credit/requests"))
                .andExpect(flash().attribute("approved", String.format("Approved credit request for %s", user.getUsername())))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());

        verify(creditRequestService).getCreditRequest(requestId);
        verify(userService).updateUserCredits(user, creditRequest.getCredits(), "Increase");
        verify(creditRequestService).deleteCreditRequest(requestId);
    }

    @Test
    void testApproveUserCreditRequestInvalidRequest() throws Exception {
        int requestId = 1;

        when(creditRequestService.getCreditRequest(requestId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(post("/admin/credit/requests/approve/{id}", requestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/credit/requests?error"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());

        verify(creditRequestService).getCreditRequest(requestId);
        verify(userService, never()).updateUserCredits(any(User.class), anyInt(), anyString());
        verify(creditRequestService, never()).deleteCreditRequest(requestId);
    }

    @Test
    void testShowCreationForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/create"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("consultant"))
                .andExpect(view().name("createConsultantPage"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testCreateConsultant() throws Exception {
        ConsultantDTO consultantDTO = new ConsultantDTO();
        consultantDTO.setName("name");
        consultantDTO.setSurname("surname");
        consultantDTO.setUsername("username");
        consultantDTO.setPassword("testpassword");

        when(consultantService.findConsultantByUsername(consultantDTO.getUsername())).thenReturn(null);

        MvcResult result = mockMvc.perform(post("/admin/create/consultant")
                        .flashAttr("consultant", consultantDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/create?success"))
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());

        verify(consultantService).findConsultantByUsername(consultantDTO.getUsername());
        verify(consultantService).saveConsultant(any(ConsultantDTO.class));
    }

    @Test
    void testCreateConsultantDuplicateUsername() throws Exception {
        ConsultantDTO consultantDTO = new ConsultantDTO();
        consultantDTO.setName("name");
        consultantDTO.setSurname("surname");
        consultantDTO.setUsername("username");
        consultantDTO.setPassword("testpassword");

        Consultant existingConsultant = new Consultant();
        existingConsultant.setName("name");
        existingConsultant.setSurname("surname");
        existingConsultant.setUsername("username");
        existingConsultant.setPassword("testpassword");

        when(consultantService.findConsultantByUsername(consultantDTO.getUsername())).thenReturn(existingConsultant);

        MvcResult result = mockMvc.perform(post("/admin/create/consultant")
                        .flashAttr("consultant", consultantDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("consultant"))
                .andExpect(view().name("createConsultantPage"))
                .andExpect(model().hasErrors())
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
        assertNotNull(result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.consultant").toString().contains("Field error"));

        verify(consultantService).findConsultantByUsername(consultantDTO.getUsername());
        verify(consultantService, never()).saveConsultant(any(ConsultantDTO.class));
    }

    @Test
    void testCreateConsultantWithErrors() throws Exception {
        ConsultantDTO consultantDTO = new ConsultantDTO();

        MvcResult result = mockMvc.perform(post("/admin/create/consultant")
                        .flashAttr("consultant", consultantDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("createConsultantPage"))
                .andExpect(model().attribute("consultant", consultantDTO))
                .andExpect(model().hasErrors())
                .andDo(print())
                .andReturn();

        assertNotNull(result);
        assertNull(result.getResolvedException());
        assertNotNull(result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.consultant").toString().contains("Field error"));
    }
}