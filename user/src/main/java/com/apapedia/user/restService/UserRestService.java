package com.apapedia.user.restService;

import com.apapedia.user.model.Customer;
import com.apapedia.user.model.Seller;
import com.apapedia.user.model.User;

import java.util.List;
import java.util.UUID;

public interface UserRestService {
    public Seller saveSeller(Seller seller);

    public Customer saveCustomer(Customer customer);

    public User getUserById(UUID id);
}
