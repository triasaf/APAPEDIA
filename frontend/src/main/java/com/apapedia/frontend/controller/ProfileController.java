package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.request.user.*;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.dto.response.user.ProfileResponseDTO;
import com.apapedia.frontend.security.jwt.JwtUtils;
import com.apapedia.frontend.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class ProfileController {
    @Autowired
    private Setting setting;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            String jwtToken = null;
            if (session != null) jwtToken = (String) session.getAttribute("token");
            if (jwtToken != null && !jwtToken.isBlank()) {
                var id = jwtUtils.getClaimFromJwtToken(jwtToken, "userId");

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("Authorization", "Bearer " + jwtToken);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<ResponseAPI<ProfileResponseDTO>> response = restTemplate.exchange(
                        setting.USER_SERVER_URL + "/me",
                        HttpMethod.GET,
                        new HttpEntity<>(httpHeaders),
                        new ParameterizedTypeReference<>() {}
                );

                if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
                    model.addAttribute("profile", response.getBody().getResult());
                } else {
                    model.addAttribute("error", response.getBody().getError());
                }
            } else {
                return "redirect:/login-sso";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        var profileDTO = new EditProfileRequestDTO();
        model.addAttribute("profileDTO", profileDTO);
        return "profile/index";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute EditProfileRequestDTO profileDTO,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            String jwtToken = null;
            if (session != null) jwtToken = (String) session.getAttribute("token");
            if (jwtToken != null && !jwtToken.isBlank()) {
                var id = jwtUtils.getClaimFromJwtToken(jwtToken, "userId");
                profileDTO.setUserId(UUID.fromString(id));

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + jwtToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                var url = setting.USER_SERVER_URL + "/profile/edit";
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<ResponseAPI> response = restTemplate.exchange(
                        setting.USER_SERVER_URL + "/profile/edit",
                        HttpMethod.PUT,
                        new HttpEntity<>(profileDTO, headers),
                        new ParameterizedTypeReference<>() {}
                );

                if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
                    redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
                } else {
                    redirectAttributes.addFlashAttribute("error", response.getBody().getError());
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping("/profile/withdraw")
    public String withdrawForm(Model model, HttpServletRequest request) {
        var balanceDTO = new UpdateBalanceRequestDTO();
        balanceDTO.setMethod("WITHDRAW");

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");
        if (jwtToken != null && !jwtToken.isBlank()) {
            var id = jwtUtils.getClaimFromJwtToken(jwtToken, "userId");

            balanceDTO.setUserId(UUID.fromString(id));
        }

        model.addAttribute("balanceDTO", balanceDTO);
        return "profile/withdraw";
    }

    @PostMapping("/profile/withdraw")
    public String withdraw(@ModelAttribute UpdateBalanceRequestDTO balanceDTO, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            String jwtToken = null;
            if (session != null) jwtToken = (String) session.getAttribute("token");
            if (jwtToken != null && !jwtToken.isBlank()) {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + jwtToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<ResponseAPI> response = restTemplate.exchange(
                        setting.USER_SERVER_URL + "/profile/update-balance",
                        HttpMethod.PUT,
                        new HttpEntity<>(balanceDTO, headers),
                        new ParameterizedTypeReference<ResponseAPI>() {}
                );

                if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
                    redirectAttributes.addFlashAttribute("success", "Your Apapay balance has been successfully withdrawn.");
                } else {
                    redirectAttributes.addFlashAttribute("error", response.getBody().getError());
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }

    //NOT USED
    @GetMapping("/profile/change-password")
    public String changePasswordForm(Model model) {
        var passwordDTO = new ChangePasswordRequestDTO();
        model.addAttribute("passwordDTO", passwordDTO);
        return "profile/change-password";
    }

    //NOT USED
    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequestDTO passwordDTO, RedirectAttributes redirectAttributes) {
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getNewPassword2())) {
            redirectAttributes.addFlashAttribute("error", "New Password doesn't match");
            return "redirect:/profile/change-password";
        }

        //TODO: connect to user service, validate user
        redirectAttributes.addFlashAttribute("success", "Your password changed successfully");
        return "redirect:/profile";
    }

    @GetMapping("/profile/delete-account")
    public String deleteAccountForm(Model model) {
        var deleteAccountDTO = new DeleteAccountRequestDTO();
        model.addAttribute("deleteAccountDTO", deleteAccountDTO);
        return "profile/delete-account";
    }

    @PostMapping("/profile/delete-account")
    public String deleteAccount(@ModelAttribute DeleteAccountRequestDTO deleteAccountDTO, RedirectAttributes redirectAttributes) {
        //TODO: connect to user service, validate user
        redirectAttributes.addFlashAttribute("Your account has been deleted");
        return "redirect:/";
    }
}

