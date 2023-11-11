package com.apapedia.user.restService;

import com.apapedia.user.dto.request.CreateCartRequestDTO;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
        var newCustomer = customerDb.save(customer);
        var cartDTO = new CreateCartRequestDTO(newCustomer.getCartId(), newCustomer.getId());

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ResponseAPI> response = restTemplate.postForEntity(Setting.CART_SERVER_URL + "/create", cartDTO, ResponseAPI.class);
        } catch (RestClientException e) {
            throw new RestClientException("Failed to create user's cart");
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
        if (user.isPresent()) return user.get();
        else throw new NoSuchElementException("User not found");
    }


}
