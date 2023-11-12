package com.apapedia.user.restController;

import com.apapedia.user.dto.UserMapper;
import com.apapedia.user.dto.request.ChangePasswordRequestDTO;
import com.apapedia.user.dto.request.CreateUserRequestDTO;
import com.apapedia.user.dto.request.EditProfileRequestDTO;
import com.apapedia.user.dto.request.UpdateBalanceRequestDTO;
import com.apapedia.user.dto.response.ResponseAPI;
import com.apapedia.user.restService.UserRestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
        ResponseAPI response = new ResponseAPI();

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
        ResponseAPI response = new ResponseAPI();
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
        ResponseAPI response = new ResponseAPI();
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
}
