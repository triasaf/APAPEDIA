package com.apapedia.user.restController;

import com.apapedia.user.dto.UserMapper;
import com.apapedia.user.dto.request.CreateUserRequestDTO;
import com.apapedia.user.dto.response.ResponseAPI;
import com.apapedia.user.restService.UserRestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
    private ResponseAPI createCustomer(@Valid @RequestBody CreateUserRequestDTO userDTO, BindingResult bindingResult) {
        var response = new ResponseAPI<>();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                res.append(error.getDefaultMessage()).append(" ");
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
        } catch (DataIntegrityViolationException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError("Username atau email tidak tersedia");
        } catch (RestClientException e) {
            response.setStatus(HttpStatus.BAD_GATEWAY.value());
            response.setMessage(HttpStatus.BAD_GATEWAY.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @GetMapping("/user/{id}")
    @ResponseStatus
    public ResponseAPI getUserById(@PathVariable(value = "id") UUID id) {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(userRestService.getUserById(id));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setError(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());

        }
        return response;
    }
}
