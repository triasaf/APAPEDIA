import 'dart:convert';

Allproducts allproductsFromJson(String str) =>
    Allproducts.fromJson(json.decode(str));

String allproductsToJson(Allproducts data) => json.encode(data.toJson());

class Allproducts {
  int status;
  String message;
  List<Result> result;

  Allproducts({
    required this.status,
    required this.message,
    required this.result,
  });

  factory Allproducts.fromJson(Map<String, dynamic> json) => Allproducts(
        status: json["status"],
        message: json["message"],
        result:
            List<Result>.from(json["result"].map((x) => Result.fromJson(x))),
      );

  Map<String, dynamic> toJson() => {
        "status": status,
        "message": message,
        "result": List<dynamic>.from(result.map((x) => x.toJson())),
      };
}

class Result {
  String id;
  String seller;
  int price;
  String productName;
  String productDescription;
  CategoryId categoryId;
  int stok;
  // List<int> image;

  Result({
    required this.id,
    required this.seller,
    required this.price,
    required this.productName,
    required this.productDescription,
    required this.categoryId,
    required this.stok,
    // required this.image,
  });

  factory Result.fromJson(Map<String, dynamic> json) => Result(
        id: json["id"],
        seller: json["seller"],
        price: json["price"],
        productName: json["productName"],
        productDescription: json["productDescription"],
        categoryId: CategoryId.fromJson(json["categoryId"]),
        stok: json["stok"],
        // image: json['image']
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "seller": seller,
        "price": price,
        "productName": productName,
        "productDescription": productDescription,
        "categoryId": categoryId.toJson(),
        "stok": stok,
        // "image": image,
      };

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Result && runtimeType == other.runtimeType && id == other.id;

  @override
  int get hashCode => id.hashCode;
}

class CategoryId {
  String id;
  String name;

  CategoryId({
    required this.id,
    required this.name,
  });

  factory CategoryId.fromJson(Map<String, dynamic> json) => CategoryId(
        id: json["id"],
        name: json["name"],
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "name": name,
      };
}
