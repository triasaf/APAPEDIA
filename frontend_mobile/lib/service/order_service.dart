import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend_mobile/models/order_response.dart';
import 'package:frontend_mobile/models/cart_response.dart';
import 'package:shared_preferences/shared_preferences.dart';

class OrderService {
  final String baseUrl;

  OrderService({required this.baseUrl});

  // TEST PAKE TOKEN
  Future<Cart> getCartByUserId() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ??
        ''; // Menggunakan operator null-aware untuk menghindari nilai null

    if (token.isEmpty) {
      throw Exception('Token is empty');
    }

    // Menetapkan header 'Authorization' dengan token JWT
    Map<String, String> headers = {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };

    final response = await http.get(
      Uri.parse('$baseUrl/api/cart'),
      headers: headers,
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body)['result'];
      return Cart.fromJson(data);
    } else {
      throw Exception('Failed to load cart by user logged in');
    }
  }

  Future<CartItem> updateCartItemQuantity(CartItem cartItem) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ??
        ''; // Menggunakan operator null-aware untuk menghindari nilai null

    if (token.isEmpty) {
      throw Exception('Token is empty');
    }

    final response = await http.put(
      Uri.parse('$baseUrl/api/cart/cart-item/update'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json'
      },
      body: json.encode(cartItem
          .toJsonForUpdate()), // Assuming you have a method toJson() in your DTO
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body)['result'];
      return CartItem.fromJson(data);
    } else {
      throw Exception('Failed to update cart item quantity');
    }
  }

  Future<String> deleteCartItem(CartItem cartItem) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ??
        ''; // Menggunakan operator null-aware untuk menghindari nilai null

    if (token.isEmpty) {
      throw Exception('Token is empty');
    }

    final cartItemId = cartItem.id;

    final response = await http.delete(
      Uri.parse('$baseUrl/api/cart/cart-item/$cartItemId/delete'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json'
      },
    );

    if (response.statusCode == 200) {
      final String data = json.decode(response.body)['result'];
      return data;
    } else {
      throw Exception('Failed to delete cart item');
    }
  }

  Future<OrderResponse> getOrderByCustomerId() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ??
        ''; // Menggunakan operator null-aware untuk menghindari nilai null

    if (token.isEmpty) {
      throw Exception('Token is empty');
    }

    // Menetapkan header 'Authorization' dengan token JWT
    Map<String, String> headers = {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };

    final response = await http.get(
      Uri.parse('$baseUrl/api/order/customer-order'),
      headers: headers,
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body);
      return OrderResponse.fromJson(data);
    } else {
      throw Exception('Failed to load order by Customer ID');
    }
  }

  Future<Order> updateOrderStatus(Order order) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ??
        ''; // Menggunakan operator null-aware untuk menghindari nilai null

    if (token.isEmpty) {
      throw Exception('Token is empty');
    }

    // Menetapkan header 'Authorization' dengan token JWT
    Map<String, String> headers = {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };

    final response = await http.put(
      Uri.parse('$baseUrl/api/order/change-status'),
      headers: headers,
      body: json.encode(order
          .toJsonForUpdate()), // Assuming you have a method toJson() in your DTO
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body)['result'];
      return Order.fromJson(data);
    } else {
      throw Exception('Failed to update order status');
    }
  }

  Future<CartItem> addCartItem(CartItem cartItemRequest) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ?? '';

    if (token.isEmpty) {
      throw Exception('Token is empty');
    }

    // Set the 'Authorization' header with the JWT token
    Map<String, String> headers = {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };

    // Encode the cart item request to JSON
    final String requestBody = json.encode(cartItemRequest.toJson());

    final response = await http.post(
      Uri.parse('$baseUrl/api/add-item'),
      headers: headers,
      body: requestBody,
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body)['result'];
      return CartItem.fromJson(data);
    } else {
      throw Exception('Failed to add cart item');
    }
  }
}
