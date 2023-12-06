package com.apapedia.user.restService;

import com.apapedia.user.dto.request.*;
import com.apapedia.user.dto.response.ResponseAPI;
import com.apapedia.user.model.Customer;
import com.apapedia.user.model.Seller;
import com.apapedia.user.model.User;
import com.apapedia.user.repository.CustomerDb;
import com.apapedia.user.repository.SellerDb;
import com.apapedia.user.repository.UserDb;
import com.apapedia.user.security.service.UserDetailsServiceImpl;
import com.apapedia.user.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserRestServiceImpl implements UserRestService{
    @Autowired
    private UserDb userDb;
    @Autowired
    private CustomerDb customerDb;
    @Autowired
    private SellerDb sellerDb;
    @Autowired
    private Setting setting;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public Customer saveCustomer(Customer customer) {
        customer.setPassword(encryptPass(customer.getPassword()));
        var newCustomer = customerDb.save(customer);
        var cartDTO = new CreateCartRequestDTO(newCustomer.getCartId(), newCustomer.getId());

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ResponseAPI> response = restTemplate.postForEntity(setting.CART_SERVER_URL + "/create", cartDTO, ResponseAPI.class);
        } catch (RestClientException e) {
            customerDb.delete(newCustomer);
            throw new RestClientException("Failed to create user's cart: " + e.getMessage());
        }
        return newCustomer;
    }

    @Override
    public Seller saveSeller(Seller seller) {
        return sellerDb.save(seller);
    }

    @Override
    public User getUserById(UUID id) {
        var user = userDb.findById(id);
        if (user.isPresent() && !user.get().getDeleted()) return user.get();
        else throw new NoSuchElementException("User not found");
    }

    @Override
    public User getUserByUsername(String username) {
        return userDb.findByUsername(username);
    }

    @Override
    public UserDetails authenticateSeller(LoginRequestDTO loginRequestDTO) {
        var userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
        var authority = userDetails.getAuthorities();

        var iterator = authority.iterator();
        if (iterator.hasNext()) {
            var authorityElement = iterator.next();
            if (!authorityElement.getAuthority().equals("SELLER")) {
                throw new UsernameNotFoundException("Username or password incorrect");
            }
        }

        return userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
    }

    @Override
    public UserDetails authenticateCustomer(LoginRequestDTO loginRequestDTO) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
            return userDetails;
        }
        throw new BadCredentialsException("Invalid username or password");
    }

    @Override
    public User updateProfile(EditProfileRequestDTO profileDTO) {
        //TODO : user validation
        var user = getUserById(profileDTO.getUserId());

        switch (profileDTO.getUpdatedAttribute().toUpperCase()) {
            case "NAME" -> {
                user.setName(profileDTO.getNewValue());
            }
            case "USERNAME" -> {
                user.setUsername(profileDTO.getNewValue());
            }
            case "EMAIL" -> {
                user.setEmail(profileDTO.getNewValue());
            }
            case "ADDRESS" -> {
                user.setAddress(profileDTO.getNewValue());
            }
            default -> {
                throw new TransactionSystemException("Invalid attribut to be update");
            }
        }
        user.setUpdatedAt(new Date());

        return userDb.save(user);
    }

    @Override
    public User updateBalance(UpdateBalanceRequestDTO balanceDTO) {
        //TODO : user validation
        if (balanceDTO.getAmount() <= 0) throw new TransactionSystemException("Amount must be positive");

        var user = getUserById(balanceDTO.getUserId());
        switch (balanceDTO.getMethod()) {
            case "TOPUP" -> {
                user.setBalance(user.getBalance() + balanceDTO.getAmount());
            }
            case "WITHDRAW" -> {
                if (user.getBalance() < balanceDTO.getAmount()) throw new TransactionSystemException("Your balance is insufficient");
                user.setBalance(user.getBalance() - balanceDTO.getAmount());
            }
            default -> {
                throw new TransactionSystemException("Invalid method");
            }
        }
        user.setUpdatedAt(new Date());
        return userDb.save(user);
    }

    @Override
    public User changePassword(ChangePasswordRequestDTO passwordDTO) {
        var user = getUserById(passwordDTO.getUserId());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new TransactionSystemException("Password incorrect");
        } else if (user.getPassword().equals(encryptPass(passwordDTO.getNewPassword()))) {
            throw new TransactionSystemException("New password must be different from old password");
        }
        user.setPassword(encryptPass(passwordDTO.getNewPassword()));
        user.setUpdatedAt(new Date());

        return userDb.save(user);
    }

    @Override
    public void deleteAccount(DeleteAccountRequestDTO deleteAccountDTO) {
        var user = getUserById(deleteAccountDTO.getUserId());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(deleteAccountDTO.getPassword(), user.getPassword())) {
            throw new TransactionSystemException("Password incorrect");
        }
        user.setDeleted(true);
        userDb.save(user);
    }

    @Override
    public boolean isUserExist(UUID id) {
        return userDb.existsById(id);
    }

    @Override
    public String encryptPass(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
