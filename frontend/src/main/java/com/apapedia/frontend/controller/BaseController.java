package com.apapedia.frontend.controller;

import com.apapedia.frontend.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {
    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        String jwtToken = (String) session.getAttribute("token");
        if (jwtToken != null && !jwtToken.isBlank()) {
            var username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            var name = jwtUtils.getClaimFromJwtToken(jwtToken, "name");

            model.addAttribute("username", username);
            model.addAttribute("name", name);
        }

        return "home";
    }
}
