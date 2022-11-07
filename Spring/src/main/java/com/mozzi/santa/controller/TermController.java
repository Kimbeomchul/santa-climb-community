package com.mozzi.santa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/terms")
public class TermController {

    @GetMapping("/location")
    public String locationTerm(Model model){
        return "terms/location_term";
    }

    @GetMapping("/personal")
    public String personalInfoTerm(Model model){
        return "terms/personal_term";
    }

    @GetMapping("/service")
    public String serviceTerm(Model model){
        return "terms/service_term";
    }

    @GetMapping("/list")
    public String termList(Model model){
        return "terms/term_list";
    }


}
