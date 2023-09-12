package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.model.User;
import com.projektas.itprojektas.model.dto.UserDTO;
import com.projektas.itprojektas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return "registerPage";
    }

    @PostMapping("/register/save")
    public String processRegister(@Valid @ModelAttribute("user") UserDTO userDTO,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", userDTO);
            return "registerPage";
        }

        User existingUser = userService.findUserByUsername(userDTO.getUsername());
        if (Objects.nonNull(existingUser) && Objects.nonNull(existingUser.getUsername()) && !existingUser.getUsername().isEmpty()) {
            result.rejectValue("username", null,
                    "There is already an account registered with the same username");
            return "registerPage";
        }

        userService.saveUser(userDTO);

        return "redirect:/registerPage?success";
    }

    @GetMapping("/logout")
    public String logout() {
        return "index";
    }
}
