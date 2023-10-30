package com.projektas.controller;

import com.projektas.itprojektas.controller.ChatController;
import com.projektas.itprojektas.model.ChatMessage;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.repository.ConsultantRepository;
import com.projektas.itprojektas.repository.ConsultationRepository;
import com.projektas.itprojektas.repository.UserRepository;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.ConsultationService;
import com.projektas.itprojektas.service.UserService;
import com.projektas.itprojektas.service.impl.ConsultantServiceImpl;
import com.projektas.itprojektas.service.impl.ConsultationServiceImpl;
import com.projektas.itprojektas.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ConsultantRepository consultantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private ConsultationService consultationService;
    private ConsultantService consultantService;
    private ChatController chatController;
    private static final double PAYMENT = 10.0;

    @BeforeEach
    void setUp() {
        consultantService = new ConsultantServiceImpl(consultantRepository, bCryptPasswordEncoder);
        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
        consultationService = new ConsultationServiceImpl(consultationRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new ChatController(consultantService, userService, consultationService)).build();
        chatController = new ChatController(consultantService, userService, consultationService);
    }

    @Test
    void testRegister() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("Test");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionAttributes(new HashMap<>());

        ChatMessage result = chatController.register(chatMessage, headerAccessor);

        assertEquals(chatMessage, result);
    }

    @Test
    void testSendMessage() {
        ChatMessage chatMessage = new ChatMessage();

        ChatMessage result = chatController.sendMessage(chatMessage);

        assertEquals(chatMessage, result);
    }

    @Test
    void testLeaveAsUserWithMatchingConsultation() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("Test");

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionAttributes(new HashMap<>());

        Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User("test", "test", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "test", authorities);

        Consultation matchingConsultation = new Consultation();
        com.projektas.itprojektas.model.User user = new com.projektas.itprojektas.model.User();
        user.setId(1);
        user.setUsername("test1");

        Consultant consultant = new Consultant();
        consultant.setUsername("test2");
        consultant.setId(1);

        matchingConsultation.setConsultant(consultant);
        matchingConsultation.setUser(user);
        matchingConsultation.setFinished(false);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(consultationRepository.findAll()).thenReturn(Collections.singletonList(matchingConsultation));

        ChatMessage result = chatController.leave(chatMessage, headerAccessor, authentication);

        verify(consultationRepository).save(matchingConsultation);
        verify(userRepository).save(matchingConsultation.getUser());
        verify(consultantRepository).save(matchingConsultation.getConsultant());
        verify(consultantRepository).freeConsultant(matchingConsultation.getConsultant().getId());

        assertEquals(chatMessage, result);
    }

    @Test
    void testLeaveAsConsultantWithMatchingConsultation() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("Test");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionAttributes(new HashMap<>());

        Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CONSULTANT"));
        UserDetails userDetails = new User("test2", "test", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "test", authorities);

        Consultation matchingConsultation = new Consultation();
        com.projektas.itprojektas.model.User user = new com.projektas.itprojektas.model.User();
        user.setId(1);
        user.setUsername("test1");

        Consultant consultant = new Consultant();
        consultant.setUsername("test2");
        consultant.setId(1);

        matchingConsultation.setConsultant(consultant);
        matchingConsultation.setUser(user);
        matchingConsultation.setFinished(false);

        when(consultantRepository.findByUsername("test2")).thenReturn(consultant);
        when(consultationRepository.findAll()).thenReturn(Collections.singletonList(matchingConsultation));

        ChatMessage result = chatController.leave(chatMessage, headerAccessor, authentication);

        verify(consultationRepository).save(matchingConsultation);
        verify(userRepository).save(matchingConsultation.getUser());
        verify(consultantRepository).save(matchingConsultation.getConsultant());
        verify(consultantRepository).freeConsultant(matchingConsultation.getConsultant().getId());

        assertEquals(chatMessage, result);
    }

    @Test
    void testLoadConsultationAsUser() throws Exception {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("Test");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionAttributes(new HashMap<>());
        Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User("test", "test", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "testPassword", authorities);

        mockMvc.perform(get("/active/consultation")
                        .principal(authentication))
                .andExpect(status().isFound())
                .andDo(print());
    }

    @Test
    void testLoadConsultationAsConsultant() throws Exception {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("Test");
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionAttributes(new HashMap<>());
        Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CONSULTANT"));
        UserDetails userDetails = new User("test", "test", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "testPassword", authorities);

        mockMvc.perform(get("/active/consultation")
                        .principal(authentication))
                .andExpect(status().isFound())
                .andDo(print());
    }
}