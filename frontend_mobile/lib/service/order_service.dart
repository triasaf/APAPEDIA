import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend_mobile/models/catalog.dart';
import 'package:frontend_mobile/models/order_response.dart';
import 'package:frontend_mobile/models/cart_response.dart';
import 'dart:typed_data';
import 'package:shared_preferences/shared_preferences.dart';

class OrderService {
  final String baseUrl;

  OrderService({required this.baseUrl});

  // TEST PAKE TOKEN
  Future<Cart> getCartByUserId(String userId) async {

    final response = await http.get(Uri.parse('$baseUrl/api/cart/?userId=$userId'));

    if(response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body)['result'];
      return Cart.fromJson(data);
    } else {
      throw Exception('Failed to load Cart by User ID');

    }
  }

  Future<CartItem> updateCartItemQuantity(CartItem cartItem) async {
    final response = await http.put(
      Uri.parse('$baseUrl/api/cart/cart-item/update'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(cartItem.toJsonForUpdate()), // Assuming you have a method toJson() in your DTO
    );

    print(response.body);

    if(response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body)['result'];
      return CartItem.fromJson(data);
    } else {
      throw Exception('Failed to update cart item quantity');

    }
  }

  Future<OrderResponse> getOrderByCustomerId() async {

    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ?? ''; // Menggunakan operator null-aware untuk menghindari nilai null

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

    if(response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body);
      return OrderResponse.fromJson(data);
    } else {
      throw Exception('Failed to load order by Customer ID');

    }
  }

  Future<Order> updateOrderStatus(Order order) async {

    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ?? ''; // Menggunakan operator null-aware untuk menghindari nilai null

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
      body: json.encode(order.toJsonForUpdate()), // Assuming you have a method toJson() in your DTO
    );

    if(response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body)['result'];
      return Order.fromJson(data);
    } else {
      throw Exception('Failed to update order status');

    }
  }

  

}
