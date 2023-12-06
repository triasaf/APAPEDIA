package com.apapedia.user.restService;

import com.apapedia.user.dto.request.*;
import com.apapedia.user.model.Customer;
import com.apapedia.user.model.Seller;
import com.apapedia.user.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserRestService {
    public Seller saveSeller(Seller seller);

    public Customer saveCustomer(Customer customer);

    public User getUserById(UUID id);

    public User getUserByUsername(String username);

    public User updateProfile(EditProfileRequestDTO profileDTO);

    public User updateBalance(UpdateBalanceRequestDTO balanceDTO);

    public User changePassword(ChangePasswordRequestDTO passwordDTO);

    public void deleteAccount(DeleteAccountRequestDTO deleteAccountDTO);

    public boolean isUserExist(UUID id);

    public String encryptPass(String password);

    public UserDetails authenticateSeller(LoginRequestDTO loginRequestDTO);

    public UserDetails authenticateCustomer(LoginRequestDTO loginRequestDTO);
}
