package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.CreateUserRequestDTO;
import com.apapedia.frontend.dto.LoginRequestDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.setting.Setting;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @GetMapping("/register")
    public String registerForm(Model model) {
        var userDTO = new CreateUserRequestDTO();
        model.addAttribute("userDTO", userDTO);

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute CreateUserRequestDTO userDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        userDTO.setRole("SELLER");

        if (!userDTO.getPassword().equals(userDTO.getPasswordConfirmation())) {
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("error", "Password doesn't match");
            return "auth/register";
        }

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ResponseAPI> result = restTemplate.postForEntity(Setting.USER_SERVER_URL + "/register", userDTO, ResponseAPI.class);
            if (result.getBody() != null && !result.getBody().getStatus().equals(200)) {
                model.addAttribute("userDTO", userDTO);
                model.addAttribute("error", result.getBody().getError());
                return "auth/register";
            }
        } catch (RestClientException e) {
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("success", "Account registration successful, please login");
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
