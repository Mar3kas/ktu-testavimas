package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;
import com.projektas.itprojektas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Objects;

@Controller
public class AuthenticationController {
    private static final String REGISTER_PAGE = "registerPage";

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "loginPage";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDTO user = new UserDTO();
        model.addAttribute("user", user);
        return REGISTER_PAGE;
    }

    @PostMapping("/register/save")
    public String processRegister(@Valid @ModelAttribute("user") UserDTO userDTO,
                                  @Nullable BindingResult result, Model model) {
        if (result != null && result.hasErrors()) {
            model.addAttribute("user", userDTO);
            return REGISTER_PAGE;
        }

        User existingUser = userService.findUserByUsername(userDTO.getUsername());
        if (Objects.nonNull(existingUser) && Objects.nonNull(existingUser.getUsername()) && !existingUser.getUsername().isEmpty()) {
            Objects.requireNonNull(result).rejectValue("username", "error.username", "There is already an account registered with the same username");
            return REGISTER_PAGE;
        }

        userService.saveUser(userDTO);

        return "redirect:/registerPage?success";
    }

    @GetMapping("/logout")
    public String logout() {
        return "index";
    }
}
