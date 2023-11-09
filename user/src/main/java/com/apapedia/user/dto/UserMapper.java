package com.apapedia.user.dto;

import com.apapedia.user.dto.request.CreateUserRequestDTO;
import com.apapedia.user.model.Customer;
import com.apapedia.user.model.Seller;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {
    Customer userRequestDTOToCustomer(CreateUserRequestDTO userDTO);

    Seller UserRequestDTOToSeller(CreateUserRequestDTO userDTO);
}
