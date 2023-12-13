package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.request.user.CreateUserRequestDTO;
import com.apapedia.frontend.dto.request.user.LoginRequestDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.security.xml.ServiceResponse;
import com.apapedia.frontend.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private Setting setting;

    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs()
                    .jaxb2Decoder(new Jaxb2XmlDecoder()))
            .build();

    @GetMapping("/register")
    public String registerForm(Model model) {
        var userDTO = new CreateUserRequestDTO();
        model.addAttribute("userDTO", userDTO);

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute CreateUserRequestDTO userDTO, Model model, RedirectAttributes redirectAttributes) {
        userDTO.setRole("SELLER");
        userDTO.setPassword("APAPEDIA");
        userDTO.setEmail(userDTO.getUsername() + "@ui.ac.id");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateUserRequestDTO> requestEntity = new HttpEntity<>(userDTO, headers);

            ResponseEntity<ResponseAPI> result = restTemplate.exchange(
                    setting.USER_SERVER_URL + "/register",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            if (result.getBody() != null && !result.getBody().getStatus().equals(200)) {
                model.addAttribute("userDTO", userDTO);
                model.addAttribute("error", result.getBody().getError());
                return "auth/register";
            }
        } catch (Exception e) {
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("success", "Account registration successful, please login");
        return "redirect:/login";
    }

    //NOT USED
    @GetMapping("/login")
    public String loginForm(Model model) {
        var loginDTO = new LoginRequestDTO();
        model.addAttribute("loginDTO", loginDTO);

        return "auth/login";
    }

    //NOT USED
    @PostMapping("/login-apapedia")
    public String login(@ModelAttribute LoginRequestDTO loginDTO, RedirectAttributes redirectAttributes, Model model) {
        loginDTO.setPassword("APAPEDIA");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDTO> requestEntity = new HttpEntity<>(loginDTO, headers);

        try {
            ResponseEntity<ResponseAPI<String>> tokenResponse = restTemplate.exchange(
                    setting.USER_SERVER_URL + "/login",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            if (tokenResponse.getBody() != null && tokenResponse.getBody().getStatus().equals(200)) {
                var token = tokenResponse.getBody().getResult();

            } else {
                model.addAttribute("loginDTO", loginDTO);
                model.addAttribute("error", tokenResponse.getBody().getError());
                return "auth/login";
            }
        } catch (Exception e) {
            model.addAttribute("loginDTO", loginDTO);
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
        return "redirect:/";
    }

    @GetMapping("/login-sso")
    public ModelAndView loginSSO() {
        return new ModelAndView("redirect:" + setting.SERVER_LOGIN + setting.FRONTEND_URL + "/validate-ticket");
    }

    @GetMapping("/validate-ticket")
    public String validateTicket(
            @RequestParam(value = "ticket", required = false) String ticket,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ServiceResponse serviceResponse = this.webClient.get().uri(
                String.format(
                        setting.SERVER_VALIDATE_TICKET,
                        ticket,
                        setting.FRONTEND_URL + "/validate-ticket"
                )
        ).retrieve().bodyToMono(ServiceResponse.class).block();

        String username = serviceResponse.getAuthenticationSuccess().getUser();

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, "APAPEDIA", null);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        try {
            LoginRequestDTO loginDTO = new LoginRequestDTO(username, "APAPEDIA");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginRequestDTO> requestEntity = new HttpEntity<>(loginDTO, headers);

            ResponseEntity<ResponseAPI<String>> tokenResponse = restTemplate.exchange(

                    setting.USER_SERVER_URL + "/login-seller",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            if (tokenResponse.getBody() != null && tokenResponse.getBody().getStatus().equals(200)) {
                var token = tokenResponse.getBody().getResult();

                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
                httpSession.setAttribute("token", token);

                redirectAttributes.addFlashAttribute("success", "Welcome, " + username + "!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Your username is not registered. Please register");
                return "redirect:/register";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }

        return "redirect:/";
    }

    @GetMapping("/logout-sso")
    public ModelAndView logoutSSO(Principal principal) {
        return new ModelAndView("redirect:" + setting.SERVER_LOGOUT + setting.FRONTEND_URL + "/logout");
    }
}
