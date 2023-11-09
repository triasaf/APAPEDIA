package com.apapedia.user.restController;

import com.apapedia.user.dto.UserMapper;
import com.apapedia.user.dto.request.CreateUserRequestDTO;
import com.apapedia.user.model.Customer;
import com.apapedia.user.model.User;
import com.apapedia.user.restService.UserRestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserRestContoller {
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register/customer")
    private Customer createCustomer(@Valid @RequestBody CreateUserRequestDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Request body has invalid type or missing field."
            );
        }

        var customer = userMapper.userRequestDTOToCustomer(userDTO);

        return userRestService.saveCustomer(customer);
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable(value = "id") UUID id) {

        return userRestService.getUserById(id);
    }
}
