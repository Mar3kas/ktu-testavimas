package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.ChatMessage;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.ConsultationService;
import com.projektas.itprojektas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class ChatControllerTest {
    @Mock
    private ConsultantService consultantService;
    @Mock
    private UserService userService;
    @Mock
    private ConsultationService consultationService;
    private ChatController chatController;
    private MockMvc mockMvc;
    private static final double PAYMENT = 10.0;

    @BeforeEach
    void setUp() {
        consultantService = mock(ConsultantService.class);
        userService = mock(UserService.class);
        consultationService = mock(ConsultationService.class);
        chatController = new ChatController(consultantService, userService, consultationService);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
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

        when(userService.findUserByUsername("test")).thenReturn(user);
        when(consultationService.getAllConsultations()).thenReturn(Collections.singletonList(matchingConsultation));

        ChatMessage result = chatController.leave(chatMessage, headerAccessor, authentication);

        verify(consultationService).updateConsultation(matchingConsultation);
        verify(userService).updateUserCredits(matchingConsultation.getUser(), PAYMENT, "Decrease");
        verify(consultantService).updateConsultantCredits(any(), eq(PAYMENT));
        verify(consultantService).freeConsultant(matchingConsultation.getUser().getId());

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

        when(consultantService.findConsultantByUsername("test2")).thenReturn(consultant);
        when(consultationService.getAllConsultations()).thenReturn(Collections.singletonList(matchingConsultation));

        ChatMessage result = chatController.leave(chatMessage, headerAccessor, authentication);

        verify(consultationService).updateConsultation(matchingConsultation);
        verify(userService).updateUserCredits(matchingConsultation.getUser(), PAYMENT, "Decrease");
        verify(consultantService).updateConsultantCredits(matchingConsultation.getConsultant(), PAYMENT);
        verify(consultantService).freeConsultant(matchingConsultation.getUser().getId());

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