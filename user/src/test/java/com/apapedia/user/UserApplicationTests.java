package com.apapedia.user;

import com.apapedia.user.dto.request.ChangePasswordRequestDTO;
import com.apapedia.user.dto.request.EditProfileRequestDTO;
import com.apapedia.user.dto.request.UpdateBalanceRequestDTO;
import com.apapedia.user.model.Customer;
import com.apapedia.user.model.Seller;
import com.apapedia.user.model.User;
import com.apapedia.user.repository.UserDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

@SpringBootTest
class UserApplicationTests {
	@Mock
	private UserDb userDb;

	@InjectMocks
	private UserRestServiceTest userRestService;

	@BeforeEach
	public void setUp() {
		List<User> users = new ArrayList<>();

		Customer customer1 = createCustomerDummy(UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee6"), "Customer 1", "customer1", "password1",
				"customer1@gmail.com", 500000, "Customer street 1");
		Customer customer2 = createCustomerDummy(UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee7"),"Customer 2", "customer2", "password2",
				"customer2@gmail.com", 200000, "Customer street 2");
		Seller seller1 = createSellerDummy(UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee8"),"Seller 1", "seller1", "password1",
				"seller1@gmail.com", 20000, "Seller street 1", "Biasa");
		Seller seller2 = createSellerDummy(UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee9"),"Seller 2", "seller2", "password2",
				"seller2@gmail.com", 200000, "Seller street 2", "Official Store");

		users.add(customer1);
		users.add(customer2);
		users.add(seller1);
		users.add(seller2);

		Mockito.when(userDb.findAll()).thenReturn(users);
	}

	@Test
	public void testGetUserByUsername() {
		String username = "customer1";
		var customer1 = userRestService.getUserByUsername(username);

		assertEquals(customer1.getName(), "Customer 1");
	}

	@Test
	public void testUpdateProfile() {
		String newName = "newCustomer1";
		String newUsername = "newCustomerUsername1";
		String newEmail = "newCustomerEmail";
		String newAddress = "New Address";


		var editProfileDTOName = new EditProfileRequestDTO(
				UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee6"),
				"NAME",
				newName);
		var editProfileDTOUsername = new EditProfileRequestDTO(
				UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee6"),
				"USERNAME",
				newUsername);
		var editProfileDTOEmail = new EditProfileRequestDTO(
				UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee6"),
				"EMAIL",
				newEmail);
		var editProfileDTOAddress = new EditProfileRequestDTO(
				UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee6"),
				"ADDRESS",
				newAddress);

		userRestService.updateProfile(editProfileDTOName);
		userRestService.updateProfile(editProfileDTOUsername);
		userRestService.updateProfile(editProfileDTOEmail);
		userRestService.updateProfile(editProfileDTOAddress);

		var updatedUser = userRestService.getUserById(UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee6"));
		assertEquals(updatedUser.getName(), "newCustomer1");
		assertEquals(updatedUser.getEmail(), "newCustomerEmail");
		assertEquals(updatedUser.getUsername(), "newCustomerUsername1");
		assertEquals(updatedUser.getAddress(), "New Address");
	}

	@Test
	public void testUpdatePassword() {
		String oldPassword = "password2";
		String newPassword = "newPassword";
		UUID userId = UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee9");

		var passwordRequestDTO = new ChangePasswordRequestDTO(userId, oldPassword, newPassword);
		userRestService.changePassword(passwordRequestDTO);

		var updatedUser = userRestService.getUserById(userId);

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
	}

	@Test
	public void testUpdateBalance() {
		Integer addedBalance = 999000;
		UUID userId = UUID.fromString("560cd164-424f-4db2-a0f3-30ed99188ee9");

		var user = userRestService.getUserById(userId);
		var oldBalance = user.getBalance();

		var updateBalanceDTO = new UpdateBalanceRequestDTO(userId, "TOPUP", addedBalance);
		userRestService.updateBalance(updateBalanceDTO);

		user = userRestService.getUserById(userId);
		assertEquals(user.getBalance(), oldBalance + addedBalance);
	}

	private Seller createSellerDummy(
			UUID id,
			String name,
			String username,
			String password,
			String email,
			long balance,
			String address,
			String category) {
		Seller seller = new Seller();
		seller.setId(id);
		seller.setName(name);
		seller.setUsername(username);
		seller.setPassword(password);
		seller.setEmail(email);
		seller.setBalance(balance);
		seller.setAddress(address);
		seller.setCreatedAt(new Date());
		seller.setUpdatedAt(new Date());
		seller.setDeleted(false);
		seller.setCategory(category);

		return seller;
	}

	private Customer createCustomerDummy(
			UUID id,
			String name,
			String username,
			String password,
			String email,
			long balance,
			String address) {
		Customer customer = new Customer();
		customer.setId(id);
		customer.setName(name);
		customer.setUsername(username);
		customer.setPassword(password);
		customer.setEmail(email);
		customer.setBalance(balance);
		customer.setAddress(address);
		customer.setCreatedAt(new Date());
		customer.setUpdatedAt(new Date());
		customer.setDeleted(false);
		customer.setCartId(UUID.randomUUID());

		return customer;
	}
}
