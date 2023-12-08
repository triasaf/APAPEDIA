package com.apapedia.user.restController;

import com.apapedia.user.dto.UserMapper;
import com.apapedia.user.dto.request.*;
import com.apapedia.user.dto.response.ResponseAPI;
import com.apapedia.user.model.Seller;
import com.apapedia.user.restService.UserRestService;
import com.apapedia.user.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserRestContoller {
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    private ResponseAPI register(@Valid @RequestBody CreateUserRequestDTO userDTO, BindingResult bindingResult) {
        var response = new ResponseAPI<>();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() -1) res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            if (userDTO.getRole().equals("SELLER")) {
                var seller = userMapper.userRequestDTOToSeller(userDTO);
                response.setResult(userRestService.saveSeller(seller));
            } else {
                var customer = userMapper.userRequestDTOToCustomer(userDTO);
                response.setResult(userRestService.saveCustomer(customer));
            }
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
        } catch (DataIntegrityViolationException | RestClientException e) {
            response.setStatus((e instanceof DataIntegrityViolationException) ? HttpStatus.BAD_REQUEST.value() : HttpStatus.BAD_GATEWAY.value());
            response.setMessage((e instanceof DataIntegrityViolationException) ? HttpStatus.BAD_REQUEST.name() : HttpStatus.BAD_GATEWAY.name());
            response.setError((e instanceof DataIntegrityViolationException) ? "Username or email already exists" : ((RestClientException) e).getMessage());
        }
        return response;
    }

    @PostMapping("/login-seller")
    public ResponseAPI login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        var response = new ResponseAPI<>();

        try {
            UserDetails userDetails = userRestService.authenticateSeller(loginRequestDTO);
            String token = jwtUtils.generateJwtToken(userDetails.getUsername());

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(token);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage(HttpStatus.UNAUTHORIZED.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @PostMapping("/login-customer")
    public ResponseAPI login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, BindingResult bindingResult) {
        var response = new ResponseAPI<>();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() -1) res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            UserDetails userDetails = userRestService.authenticateCustomer(loginRequestDTO);
            String token = jwtUtils.generateJwtToken(userDetails.getUsername());

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(token);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage(HttpStatus.UNAUTHORIZED.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @GetMapping("/user/{id}")
    public ResponseAPI getUserById(@PathVariable(value = "id") UUID id) {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(userRestService.getUserById(id));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @PutMapping("/profile/edit")
    public ResponseAPI editProfile(@Valid @RequestBody EditProfileRequestDTO profileDTO, BindingResult bindingResult) {
        var response = new ResponseAPI();

        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() -1) res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(userRestService.updateProfile(profileDTO));
        } catch (DataIntegrityViolationException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(profileDTO.getUpdatedAttribute() + " is not available");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }

        return response;
    }

    @PutMapping("/profile/update-balance")
    public ResponseAPI updateBalance(@Valid @RequestBody UpdateBalanceRequestDTO balanceDTO, BindingResult bindingResult) {
        var response = new ResponseAPI();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() -1) res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(userRestService.updateBalance(balanceDTO));
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }

        return response;
    }

    @PutMapping("/profile/change-password")
    public ResponseAPI changePassword(@Valid @RequestBody ChangePasswordRequestDTO passwordDTO, BindingResult bindingResult) {
        var response = new ResponseAPI();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() -1) res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(userRestService.changePassword(passwordDTO));
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }

        return response;
    }

    @DeleteMapping("/profile/delete-account")
    public ResponseAPI deleteAccount(@Valid @RequestBody DeleteAccountRequestDTO deleteAccountDTO, BindingResult bindingResult) {
        var response = new ResponseAPI<>();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() -1) res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }
        try {
            userRestService.deleteAccount(deleteAccountDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult("Your account has been deleted");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setResult(e.getMessage());
        }

        return response;
    }

    @GetMapping("/user/is-exist/{id}")
    public ResponseAPI isUserExist(@PathVariable(value = "id") UUID id) {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(userRestService.isUserExist(id));
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setResult(e.getMessage());
        }
        return response;
    }

    @GetMapping("/me/{id}")
    public ResponseAPI getMyInfo(@PathVariable(value = "id") UUID id) {
        var response = new ResponseAPI<>();

        if (id != null) {
            var user = userRestService.getUserById(id);
            var profileDTO = userMapper.userToProfileResponseDTO(user);
            if (user instanceof Seller) {
                profileDTO.setCategory(((Seller) user).getCategory());
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(profileDTO);
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setResult("You are not authenticated. Please login first");
        }

        return response;
    }
}
