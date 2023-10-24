package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.Consultant;
import com.projektas.itprojektas.model.CreditRequest;
import com.projektas.itprojektas.model.dto.ConsultantDTO;
import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.service.ConsultantService;
import com.projektas.itprojektas.service.CreditRequestService;
import com.projektas.itprojektas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CreditRequestService creditRequestService;
    private final ConsultantService consultantService;
    private final UserService userService;

    @Autowired
    public AdminController(CreditRequestService creditRequestService,
                           ConsultantService consultantService,
                           UserService userService) {

        this.creditRequestService = creditRequestService;
        this.consultantService = consultantService;
        this.userService = userService;
    }


    @GetMapping("/credit/requests")
    public String viewAllUsersWithCreditRequests(Model model) {
        List<CreditRequest> creditRequestList = creditRequestService.getAllCreditRequests();
        model.addAttribute("requests", creditRequestList);
        return "adminCreditRequestsPage";
    }

    @DeleteMapping("/credit/requests/reject/{id}")
    public String rejectUserCreditRequest(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<CreditRequest> creditRequest = creditRequestService.getCreditRequest(id);
        if (creditRequest.isPresent()) {
            User user = creditRequest.get().getUser();
            creditRequestService.deleteCreditRequest(id);
            redirectAttributes.addFlashAttribute("rejected", String.format("Credit request for %s was rejected", user.getUsername()));
            return "redirect:/admin/credit/requests";
        }
        return "redirect:/admin/credit/requests?error";
    }

    @PostMapping("/credit/requests/approve/{id}")
    public String approveUserCreditRequest(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<CreditRequest> creditRequest = creditRequestService.getCreditRequest(id);
        if (creditRequest.isPresent()) {
            User user = creditRequest.get().getUser();
            userService.updateUserCredits(user, creditRequest.get().getCredits(), "Increase");
            creditRequestService.deleteCreditRequest(id);
            redirectAttributes.addFlashAttribute("approved", String.format("Approved credit request for %s", user.getUsername()));
            return "redirect:/admin/credit/requests";
        }
        return "redirect:/admin/credit/requests?error";
    }

    @GetMapping("/create")
    public String showCreationForm(Model model) {
        ConsultantDTO consultant = new ConsultantDTO();
        model.addAttribute("consultant", consultant);
        return "createConsultantPage";
    }

    @PostMapping("/create/consultant")
    public String createConsultant(@Valid @ModelAttribute("consultant") ConsultantDTO consultantDTO,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("consultant", consultantDTO);
            return "createConsultantPage";
        }

        Consultant existingConsultant = consultantService.findConsultantByUsername(consultantDTO.getUsername());
        if (Objects.nonNull(existingConsultant) && Objects.nonNull(existingConsultant.getUsername()) && !existingConsultant.getUsername().isEmpty()) {
            result.rejectValue("username", null,
                    "There is already an account registered with the same username");
            return "createConsultantPage";
        }

        consultantService.saveConsultant(consultantDTO);

        return "redirect:/admin/create?success";
    }
}