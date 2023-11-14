package com.apapedia.user.restService;

import com.apapedia.user.dto.request.ChangePasswordRequestDTO;
import com.apapedia.user.dto.request.CreateCartRequestDTO;
import com.apapedia.user.dto.request.EditProfileRequestDTO;
import com.apapedia.user.dto.request.UpdateBalanceRequestDTO;
import com.apapedia.user.dto.response.ResponseAPI;
import com.apapedia.user.model.Customer;
import com.apapedia.user.model.Seller;
import com.apapedia.user.model.User;
import com.apapedia.user.repository.CustomerDb;
import com.apapedia.user.repository.SellerDb;
import com.apapedia.user.repository.UserDb;
import com.apapedia.user.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Override
    public Customer saveCustomer(Customer customer) {
        //TODO : password encryption
        var newCustomer = customerDb.save(customer);
        var cartDTO = new CreateCartRequestDTO(newCustomer.getCartId(), newCustomer.getId());

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ResponseAPI> response = restTemplate.postForEntity(Setting.CART_SERVER_URL + "/create", cartDTO, ResponseAPI.class);
        } catch (RestClientException e) {
            customerDb.delete(newCustomer);
            throw new RestClientException("Failed to create user's cart: " + e.getMessage());
        }
        return newCustomer;
    }

    @Override
    public Seller saveSeller(Seller seller) {
        //TODO : password encrtpyion
        return sellerDb.save(seller);
    }

    @Override
    public User getUserById(UUID id) {
        //TODO : user validation
        var user = userDb.findById(id);
        if (user.isPresent()) return user.get();
        else throw new NoSuchElementException("User not found");
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

        //TODO : password encryption
        if (!user.getPassword().equals(passwordDTO.getOldPassword())) {
            throw new TransactionSystemException("Password incorrect");
        } else if (user.getPassword().equals(passwordDTO.getNewPassword())) {
            throw new TransactionSystemException("New password must be different from old password");
        }
        //TODO : password encryption
        user.setPassword(passwordDTO.getNewPassword());
        user.setUpdatedAt(new Date());

        return userDb.save(user);
    }
}
