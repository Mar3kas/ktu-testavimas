package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.ChatMessage;
import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.ConsultationService;
import com.projektas.itprojektas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ChatController {
    private static final double PAYMENT = 10.0;
    private final ConsultantService consultantService;
    private final UserService userService;
    private final ConsultationService consultationService;

    @Autowired
    public ChatController(
            ConsultantService consultantService,
            UserService userService,
            ConsultationService consultationService
    ) {
        this.consultantService = consultantService;
        this.userService = userService;
        this.consultationService = consultationService;
    }

    @MessageMapping("/chat.register")
    @SendTo("/topic/public")
    public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.leave")
    @SendTo("/topic/public")
    public ChatMessage leave(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, Authentication authentication) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());

        List<Consultation> consultationList = consultationService.getAllConsultations();
        Collection<? extends GrantedAuthority> role = authentication.getAuthorities();

        if (role.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            String username = authentication.getName();
            User user = userService.findUserByUsername(username);

            Optional<Consultation> matchingConsultation = consultationList.stream()
                    .filter(consultation -> consultation.getUser().getId() == user.getId() && !consultation.isFinished())
                    .findFirst();

            if (matchingConsultation.isPresent()) {
                Consultation consultation = matchingConsultation.get();
                consultationService.updateConsultation(consultation);
                userService.updateUserCredits(user, PAYMENT, "Decrease");
                consultantService.updateConsultantCredits(consultation.getConsultant(), PAYMENT);
                consultantService.freeConsultant(consultation.getConsultant().getId());
            }
        }

        if (role.contains(new SimpleGrantedAuthority("ROLE_CONSULTANT"))) {
            String username = authentication.getName();
            Consultant consultant = consultantService.findConsultantByUsername(username);

            Optional<Consultation> matchingConsultation = consultationList.stream()
                    .filter(consultation -> consultation.getConsultant().getId() == consultant.getId() && !consultation.isFinished())
                    .findFirst();

            if (matchingConsultation.isPresent()) {
                Consultation consultation = matchingConsultation.get();
                consultationService.updateConsultation(consultation);
                userService.updateUserCredits(consultation.getUser(), PAYMENT, "Decrease");
                consultantService.updateConsultantCredits(consultant, PAYMENT);
                consultantService.freeConsultant(consultation.getUser().getId());
            }
        }

        return chatMessage;
    }

    @GetMapping("/active/consultation")
    public String loadConsultation(Authentication authentication) {
        Collection<? extends GrantedAuthority> role = authentication.getAuthorities();
        List<Consultation> consultationList = consultationService.getAllConsultations();

        if (role.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            String username = authentication.getName();
            User user = userService.findUserByUsername(username);

            Optional<Consultation> matchingConsultation = consultationList.stream()
                    .filter(consultation -> consultation.getUser().getId() == user.getId() && !consultation.isFinished())
                    .findFirst();

            if (matchingConsultation.isPresent()) {
                return "chatPage";
            }
        }

        if (role.contains(new SimpleGrantedAuthority("ROLE_CONSULTANT"))) {
            String username = authentication.getName();
            Consultant consultant = consultantService.findConsultantByUsername(username);

            Optional<Consultation> matchingConsultation = consultationList.stream()
                    .filter(consultation -> consultation.getConsultant().getId() == consultant.getId() && !consultation.isFinished())
                    .findFirst();

            if (matchingConsultation.isPresent()) {
                return "chatPage";
            }
        }

        return "redirect:/?error";
    }
}