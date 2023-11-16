package com.projektas.controller;

import com.projektas.itprojektas.controller.AdminController;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.CreditRequest;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.ConsultantDTO;
import com.projektas.itprojektas.repository.ConsultantRepository;
import com.projektas.itprojektas.repository.CreditRequestRepository;
import com.projektas.itprojektas.repository.UserRepository;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.CreditRequestService;
import com.projektas.itprojektas.service.UserService;
import com.projektas.itprojektas.service.impl.ConsultantServiceImpl;
import com.projektas.itprojektas.service.impl.CreditRequestServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
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
class AdminControllerTest {
    private MockMvc mockMvc;
    @Mock
    private CreditRequestRepository creditRequestRepository;
    @Mock
    private ConsultantRepository consultantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ConsultantService consultantService;
    private UserService userService;
    private CreditRequestService creditRequestService;

    @BeforeEach
    public void setup() {
        creditRequestService = new CreditRequestServiceImpl(creditRequestRepository);
        consultantService = new ConsultantServiceImpl(consultantRepository, bCryptPasswordEncoder);
        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminController(creditRequestService, consultantService, userService)).build();
    }

    @Test
    void testViewAllUsersWithCreditRequests() throws Exception {
        List<CreditRequest> creditRequestList = new ArrayList<>();

        when(creditRequestRepository.findAll()).thenReturn(creditRequestList);

        MvcResult result = mockMvc.perform(get("/admin/credit/requests"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("requests", creditRequestList))
                .andExpect(view().name("adminCreditRequestsPage"))
                .andDo(print())
                .andReturn();

        verify(creditRequestRepository).findAll();

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testRejectUserCreditRequest() throws Exception {
        int requestId = 1;
        User user = new User();
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setUser(user);

        when(creditRequestRepository.findById(requestId)).thenReturn(Optional.of(creditRequest));

        MvcResult result = mockMvc.perform(delete("/admin/credit/requests/reject/{id}", requestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/credit/requests"))
                .andExpect(flash().attribute("rejected", String.format("Credit request for %s was rejected", user.getUsername())))
                .andDo(print())
                .andReturn();

        verify(creditRequestRepository).findById(requestId);
        verify(creditRequestRepository).deleteById(requestId);

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testRejectUserCreditRequestInvalidRequest() throws Exception {
        int requestId = 1;

        when(creditRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(delete("/admin/credit/requests/reject/{id}", requestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/credit/requests?error"))
                .andDo(print())
                .andReturn();

        verify(creditRequestRepository).findById(requestId);
        verify(creditRequestRepository, never()).deleteById(requestId);

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testApproveUserCreditRequest() throws Exception {
        int requestId = 1;
        User user = new User();
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setUser(user);

        when(creditRequestRepository.findById(requestId)).thenReturn(Optional.of(creditRequest));

        MvcResult result = mockMvc.perform(post("/admin/credit/requests/approve/{id}", requestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/credit/requests"))
                .andExpect(flash().attribute("approved", String.format("Approved credit request for %s", user.getUsername())))
                .andDo(print())
                .andReturn();

        verify(creditRequestRepository).findById(requestId);
        verify(userRepository).save(user);
        verify(creditRequestRepository).deleteById(requestId);

        assertNotNull(result);
        assertNull(result.getResolvedException());
    }

    @Test
    void testApproveUserCreditRequestInvalidRequest() throws Exception {
        int requestId = 1;

        when(creditRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(post("/admin/credit/requests/approve/{id}", requestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/credit/requests?error"))
                .andDo(print())
                .andReturn();

        verify(creditRequestRepository).findById(requestId);
        verify(userRepository, never()).save(any(User.class));
        verify(creditRequestRepository, never()).deleteById(requestId);

        assertNotNull(result);
        assertNull(result.getResolvedException());
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

        Consultant consultant = new Consultant();
        consultant.setName("name");
        consultant.setSurname("surname");
        consultant.setUsername("username");
        consultant.setPassword("testpassword");

        when(consultantRepository.findByUsername(consultantDTO.getUsername())).thenReturn(null);
        when(consultantRepository.save(any(Consultant.class))).thenReturn(consultant);
        when(bCryptPasswordEncoder.encode(consultantDTO.getPassword())).thenReturn("encodedPassword");

        MvcResult result = mockMvc.perform(post("/admin/create/consultant")
                        .flashAttr("consultant", consultantDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/create?success"))
                .andDo(print())
                .andReturn();

        verify(consultantRepository).findByUsername(consultantDTO.getUsername());
        verify(consultantRepository).save(any(Consultant.class));

        assertNotNull(result);
        assertNull(result.getResolvedException());
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

        when(consultantRepository.findByUsername(consultantDTO.getUsername())).thenReturn(existingConsultant);

        MvcResult result = mockMvc.perform(post("/admin/create/consultant")
                        .flashAttr("consultant", consultantDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("consultant"))
                .andExpect(view().name("createConsultantPage"))
                .andExpect(model().hasErrors())
                .andDo(print())
                .andReturn();

        verify(consultantRepository).findByUsername(consultantDTO.getUsername());
        verify(consultantRepository, never()).save(any(Consultant.class));

        assertNotNull(result);
        assertNull(result.getResolvedException());

        ModelAndView modelAndView = result.getModelAndView();
        assertNotNull(modelAndView);
        ModelMap modelMap = (ModelMap) modelAndView.getModel();
        assertNotNull(modelMap);
        Object consultantBindingResult = modelMap.get("org.springframework.validation.BindingResult.consultant");
        assertNotNull(consultantBindingResult);
        assertTrue(consultantBindingResult instanceof BindingResult);
        assertTrue(((BindingResult) consultantBindingResult).hasFieldErrors());
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

        ModelAndView modelAndView = result.getModelAndView();
        assertNotNull(modelAndView);
        ModelMap modelMap = (ModelMap) modelAndView.getModel();
        assertNotNull(modelMap);
        Object consultantBindingResult = modelMap.get("org.springframework.validation.BindingResult.consultant");
        assertNotNull(consultantBindingResult);
        assertTrue(consultantBindingResult instanceof BindingResult);
        assertTrue(((BindingResult) consultantBindingResult).hasFieldErrors());
    }
}