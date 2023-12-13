// To parse this JSON data, do
//
//     final orderResponse = orderResponseFromJson(jsonString);

import 'dart:convert';

OrderResponse orderResponseFromJson(String str) => OrderResponse.fromJson(json.decode(str));

String orderResponseToJson(OrderResponse data) => json.encode(data.toJson());

class OrderResponse {
    int status;
    String message;
    List<Order> order;

    OrderResponse({
        required this.status,
        required this.message,
        required this.order,
    });

    factory OrderResponse.fromJson(Map<String, dynamic> json) => OrderResponse(
        status: json["status"],
        message: json["message"],
        order: List<Order>.from(json["result"].map((x) => Order.fromJson(x))),
    );

    Map<String, dynamic> toJson() => {
        "status": status,
        "message": message,
        "order": List<dynamic>.from(order.map((x) => x.toJson())),
    };
}

class Order {
    String id;
    DateTime createdAt;
    DateTime updatedAt;
    int status;
    int totalPrice;
    String customer;
    String seller;
    List<OrderItem> listOrderItem;

    Order({
        required this.id,
        required this.createdAt,
        required this.updatedAt,
        required this.status,
        required this.totalPrice,
        required this.customer,
        required this.seller,
        required this.listOrderItem,
    });

    factory Order.fromJson(Map<String, dynamic> json) => Order(
        id: json["id"],
        createdAt: DateTime.parse(json["createdAt"]),
        updatedAt: DateTime.parse(json["updatedAt"]),
        status: json["status"],
        totalPrice: json["totalPrice"],
        customer: json["customer"],
        seller: json["seller"],
        listOrderItem: List<OrderItem>.from(json["listOrderItem"].map((x) => OrderItem.fromJson(x))),
    );

    Map<String, dynamic> toJson() => {
        "id": id,
        "createdAt": createdAt.toIso8601String(),
        "updatedAt": updatedAt.toIso8601String(),
        "status": status,
        "totalPrice": totalPrice,
        "customer": customer,
        "seller": seller,
        "listOrderItem": List<dynamic>.from(listOrderItem.map((x) => x.toJson())),
    };

    Map<String, dynamic> toJsonForUpdate() => {
        "id": id,
        "status": status,
    };
}

class OrderItem {
    String id;
    String productId;
    int quantity;
    String productName;
    int productPrice;

    OrderItem({
        required this.id,
        required this.productId,
        required this.quantity,
        required this.productName,
        required this.productPrice,
    });

    factory OrderItem.fromJson(Map<String, dynamic> json) => OrderItem(
        id: json["id"],
        productId: json["productId"],
        quantity: json["quantity"],
        productName: json["productName"],
        productPrice: json["productPrice"],
    );

    Map<String, dynamic> toJson() => {
        "id": id,
        "productId": productId,
        "quantity": quantity,
        "productName": productName,
        "productPrice": productPrice,
    };
}
