package com.apapedia.user.restService;

import com.apapedia.user.dto.request.ChangePasswordRequestDTO;
import com.apapedia.user.dto.request.EditProfileRequestDTO;
import com.apapedia.user.dto.request.UpdateBalanceRequestDTO;
import com.apapedia.user.model.Customer;
import com.apapedia.user.model.Seller;
import com.apapedia.user.model.User;

import java.util.UUID;

public interface UserRestService {
    public Seller saveSeller(Seller seller);

    public Customer saveCustomer(Customer customer);

    public User getUserById(UUID id);

    public User updateProfile(EditProfileRequestDTO profileDTO);

    public User updateBalance(UpdateBalanceRequestDTO balanceDTO);

    public User changePassword(ChangePasswordRequestDTO passwordDTO);
}
