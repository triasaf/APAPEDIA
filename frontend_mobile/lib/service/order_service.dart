import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend_mobile/models/catalog.dart';
import 'package:frontend_mobile/models/cart_response.dart';
import 'dart:typed_data';

class OrderService {
  final String baseUrl;

  OrderService({required this.baseUrl});


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

  

}
