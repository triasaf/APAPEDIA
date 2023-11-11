package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.CreateUserRequestDTO;
import com.apapedia.frontend.dto.LoginRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    @GetMapping("/register")
    public String registerForm(Model model) {
        var userDTO = new CreateUserRequestDTO();
        model.addAttribute("userDTO", userDTO);

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute CreateUserRequestDTO userDTO) {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        var loginDTO = new LoginRequestDTO();
        model.addAttribute("loginDTO", loginDTO);

        return "auth/login";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        return "profile/index";
    }
}
