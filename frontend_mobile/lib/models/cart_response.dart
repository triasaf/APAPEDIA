import 'dart:convert';

CartResponse cartResponseFromJson(String str) =>
    CartResponse.fromJson(json.decode(str));

String cartResponseToJson(CartResponse data) => json.encode(data.toJson());

class CartResponse {
  int status;
  String message;
  Cart cart;

  CartResponse({
    required this.status,
    required this.message,
    required this.cart,
  });

  factory CartResponse.fromJson(Map<String, dynamic> json) => CartResponse(
        status: json["status"],
        message: json["message"],
        cart: Cart.fromJson(json["cart"]),
      );

  Map<String, dynamic> toJson() => {
        "status": status,
        "message": message,
        "cart": cart.toJson(),
      };
}

class Cart {
  String id;
  String userId;
  int totalPrice;
  List<CartItem> listCartItem;

  Cart({
    required this.id,
    required this.userId,
    required this.totalPrice,
    required this.listCartItem,
  });

  factory Cart.fromJson(Map<String, dynamic> json) => Cart(
        id: json["id"],
        userId: json["userId"],
        totalPrice: json["totalPrice"],
        listCartItem: List<CartItem>.from(
            json["listCartItem"].map((x) => CartItem.fromJson(x))),
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "userId": userId,
        "totalPrice": totalPrice,
        "listCartItem": List<dynamic>.from(listCartItem.map((x) => x.toJson())),
      };
}

class CartItem {
  String id;
  String productId;
  int quantity;

  CartItem({
    required this.id,
    required this.productId,
    required this.quantity,
  });

  // Modify the toJson method to exclude productId to use in DTO for request body
  Map<String, dynamic> toJsonForUpdate() {
    return {
      "id": id,
      "quantity": quantity,
    };
  }

  factory CartItem.fromJson(Map<String, dynamic> json) => CartItem(
        id: json["id"],
        productId: json["productId"],
        quantity: json["quantity"],
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "productId": productId,
        "quantity": quantity,
      };
}
