package com.apapedia.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.apapedia.order.dto.request.CatalogDTO;
import com.apapedia.order.dto.request.CategoryDTO;
import com.apapedia.order.dto.request.UpdateCartItemRequestDTO;
import com.apapedia.order.dto.request.UpdateOrderRequestDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.model.Order;
import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.CartItemDb;
import com.apapedia.order.repository.OrderDb;
import com.apapedia.order.restService.CartRestServiceImpl;
import com.apapedia.order.restService.OrderRestServiceImpl;
import com.apapedia.order.setting.Setting;

@SpringBootTest
class OrderApplicationTests {

	@Mock
	OrderDb orderDb;

	@Mock
	CartDb cartDb;

	@Mock
	CartItemDb cartItemDb;

	@Mock
    RestTemplate restTemplate; // Mock RestTemplate since it's not easily 
	
	@Mock
	Setting setting;

	@InjectMocks
	OrderRestServiceImpl orderService;

	@InjectMocks
	CartRestServiceImpl cartService;

	private UUID userId;
	private UUID sellerId;

	@BeforeEach
	public void setUp() {
		UUID cartId = UUID.randomUUID();
		userId = UUID.randomUUID();
		sellerId = UUID.randomUUID();
		Integer totalPrice = 0;

		List<Cart> listCart = new ArrayList<>();
		List<CartItem> listCartItems = new ArrayList<>();
		List<Order> listOrders = new ArrayList<>();

		
		Cart cart = new Cart(cartId, userId, totalPrice, listCartItems);
		// listCart.add(cart);

		UUID productId1 = UUID.randomUUID();		
		UUID productId2 = UUID.randomUUID();

		UUID cartItemId1 = UUID.randomUUID();		
		UUID cartItemId2 = UUID.randomUUID();

		UUID orderId = UUID.randomUUID();

		CartItem cartItem1 = new CartItem(cartItemId1, productId1, cart, 2);		
		CartItem cartItem2 = new CartItem(cartItemId2, productId2, cart, 2);
		listCartItems.add(cartItem1);		
		listCartItems.add(cartItem2);

		Cart cartNew = new Cart(cartId, userId, totalPrice, listCartItems);
		listCart.add(cartNew);

		Order order = new Order(orderId, new Date(), new Date(), 0, 1000, userId, sellerId, new ArrayList<>());
		listOrders.add(order);

		Mockito.when(cartDb.findAll()).thenReturn(listCart);		
		Mockito.when(cartItemDb.findAll()).thenReturn(listCartItems);
		Mockito.when(orderDb.findAll()).thenReturn(listOrders);

		// Mock the delete method to remove from the list
		Mockito.doAnswer(invocation -> {
			CartItem deletedItem = invocation.getArgument(0);
			listCartItems.remove(deletedItem);
			return null;
		}).when(cartItemDb).delete(Mockito.any(CartItem.class));


		// Mock the behavior of the setting field
        Mockito.when(setting.getCatalogServerUrl()).thenReturn("http://example.com/catalog");  

    }
	

	// @Test
	// void testUpdateCartItemQuantity() {

		

	// 	List<CartItem> listCartItems = cartItemDb.findAll();

	// 	CartItem cartItem1 = listCartItems.get(0);


    //     UpdateCartItemRequestDTO updateCartItemDTO = new UpdateCartItemRequestDTO();
    //     updateCartItemDTO.setId(cartItem1.getId());
    //     updateCartItemDTO.setQuantity(5);

	// 	ResponseAPI response = new ResponseAPI<>();

	// 	var catalogDTO = new CatalogDTO(UUID.randomUUID(), UUID.randomUUID(), 1, "test", "test", new CategoryDTO(), 100000);

	// 	response.setStatus(200);		
	// 	response.setMessage("OK");
	// 	response.setResult(catalogDTO);


	// 	// Mock the behavior of RestTemplate
    //     ResponseEntity<ResponseAPI<CatalogDTO>> mockResponseEntity = new ResponseEntity<>(
    //             response, HttpStatus.OK);

    //     when(restTemplate.exchange(
	// 			eq("http://apap-189.cs.ui.ac.id/api/catalog/"),
    //             Mockito.eq(org.springframework.http.HttpMethod.GET),
    //             Mockito.isNull(),
    //             Mockito.<ParameterizedTypeReference<ResponseAPI<CatalogDTO>>>any()
    //     )).thenReturn(mockResponseEntity);

	// 	// Perform the test
    //     CartItem updatedCartItem = cartService.updateCartItemQuantity(updateCartItemDTO);
	// 	assertEquals(5, updatedCartItem.getQuantity());


	// }

	@Test
	void testDeleteCartItem() {
		
		List<CartItem> listCartItemsBeforeDelete = cartItemDb.findAll();

		CartItem cartItem1 = listCartItemsBeforeDelete.get(0);

		Mockito.when(cartItemDb.findById(cartItem1.getId())).thenReturn(Optional.of(cartItem1));

		cartService.deleteCartItem(cartItem1.getId());

		Mockito.verify(cartItemDb, Mockito.times(1)).delete(cartItem1);

		// Fetch the list of cart items after deletion
		List<CartItem> listCartItemsAfterDeletion = cartItemDb.findAll();

		// Assert that cartItem1 is no longer in the list after deletion
    	assertFalse(listCartItemsAfterDeletion.contains(cartItem1));


	}

	@Test
	void testGetCartItemByUserId() {
		List<Cart> listCarts = cartDb.findAll();

		Cart cartFromMock = listCarts.get(0);

		List<CartItem> listCartItemsFromMock = cartItemDb.findAll();

		Cart cartFromService = cartService.findCartByUserId(userId);

		List<CartItem> listCartItemsFromService = cartFromService.getListCartItem();

		// Add assertions to verify the expected behavior
		assertNotNull(cartFromService);
		assertEquals(userId, cartFromService.getUserId()); // Assuming getUserId() returns the user ID from the cart

		// Example assertion for comparing the number of cart items
		assertEquals(listCartItemsFromMock.size(), listCartItemsFromService.size());

		
		assertEquals(cartFromMock, cartFromService);


		// You may need to add more specific assertions based on your test logic and expectations

		// For example, if you have a CartItem.equals() method, you can check if the lists contain the same items
		assertTrue(listCartItemsFromMock.containsAll(listCartItemsFromService));
	}

	@Test
	void testPutOrderStatus() {
		List<Order> listOrders = orderDb.findAll();
		Order order = listOrders.get(0);

		UpdateOrderRequestDTO orderDTO = new UpdateOrderRequestDTO();
		orderDTO.setId(order.getId());
		orderDTO.setStatus(3);

		// Mock the behavior of orderDb.save() or orderDb.update()
		when(orderDb.save(any(Order.class))).thenReturn(order);

		// Act
		Order updatedOrder = orderService.changeStatusOrder(orderDTO);
	
		// Assert
		assertNotNull(updatedOrder);
		assertEquals(3, updatedOrder.getStatus());

	}





}
