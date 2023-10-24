package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.Consultation;
import com.projektas.itprojektas.model.dto.CreditRequestDTO;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.ConsultationService;
import com.projektas.itprojektas.service.CreditRequestService;
import com.projektas.itprojektas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class UserController {

    private final ConsultationService consultationService;
    private final ConsultantService consultantService;
    private final CreditRequestService creditRequestService;
    private final UserService userService;

    @Autowired
    public UserController(
            ConsultationService consultationService,
            ConsultantService consultantService,
            CreditRequestService creditRequestService,
            UserService userService
    ) {
        this.consultationService = consultationService;
        this.consultantService = consultantService;
        this.creditRequestService = creditRequestService;
        this.userService = userService;
    }

    @GetMapping("/credit")
    public String viewCreditRequestForm(Model model) {
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        model.addAttribute("creditRequestDTO", creditRequestDTO);
        return "creditRequestPage";
    }

    @PostMapping("/credit/request")
    public String createCreditRequest(
            @Valid @ModelAttribute("creditRequestDTO") CreditRequestDTO creditRequestDTO,
            BindingResult result,
            Model model,
            Authentication authentication
    ) {
        if (result.hasErrors()) {
            model.addAttribute("creditRequestDTO", creditRequestDTO);
            return "creditRequestPage";
        }

        String username = authentication.getName();
        User user = userService.findUserByUsername(username);
        creditRequestDTO.setUser(user);
        creditRequestService.saveCreditRequest(creditRequestDTO);
        return "redirect:/credit?success";
    }

    @PostMapping("/reserve/consultant/{id}")
    public String reserveConsultantLoadLiveChat(
            @PathVariable int id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String username = authentication.getName();
        User user = userService.findUserByUsername(username);

        if (Objects.isNull(user)) {
            redirectAttributes.addFlashAttribute(
                    "rejected",
                    String.format("User doesn't exist")
            );
            return "redirect:/";
        }

        if (user.getCredits() < 10) {
            redirectAttributes.addFlashAttribute(
                    "rejected",
                    String.format("You do not have enough credits. You need at least 10. You have %s", user.getCredits())
            );
            return "redirect:/";
        }

        List<Consultation> consultationList = consultationService.getAllConsultations();

        Optional<Consultation> matchingConsultation = consultationList.stream()
                .filter(consultation -> consultation.getUser().getId() == user.getId() && !consultation.isFinished())
                .findFirst();

        if (matchingConsultation.isPresent()) {
            redirectAttributes.addFlashAttribute("rejected", "Can't have more than one active consultation");
            return "redirect:/";
        }

        Consultant consultant = consultantService.findConsultantById(id);

        if (Objects.isNull(consultant)) {
            redirectAttributes.addFlashAttribute(
                    "rejected",
                    String.format("Consultant doesn't exist")
            );
            return "redirect:/";
        }

        consultationService.saveConsultation(user, consultant);
        consultantService.occupyConsultant(id);
        return "redirect:/?success";
    }
}
