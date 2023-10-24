package com.projektas.itprojektas.controller;

import com.projektas.itprojektas.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    private final ConsultantService consultantService;

    @Autowired
    public CommonController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("listConsultants", consultantService.getAllConsultants());
        return "index";
    }

    @GetMapping("/help")
    public String viewHelpPage() {
        return "helpPage";
    }

    @GetMapping("/consultation")
    public String openLiveChat(Model model) {
        return "chatPage";
    }
}
