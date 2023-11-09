package com.apapedia.user.restService;

import com.apapedia.user.model.Customer;
import com.apapedia.user.model.Seller;
import com.apapedia.user.model.User;
import com.apapedia.user.repository.CustomerDb;
import com.apapedia.user.repository.SellerDb;
import com.apapedia.user.repository.UserDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return customerDb.save(customer);
    }

    @Override
    public Seller saveSeller(Seller seller) {
        return sellerDb.save(seller);
    }

    @Override
    public User getUserById(UUID id) {
        var user = userDb.findById(id);
        if (user.isPresent()) return user.get();
        else throw new NoSuchElementException("User not found");
    }

}
