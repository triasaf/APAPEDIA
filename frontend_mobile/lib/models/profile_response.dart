import 'dart:convert';

ProfileResponse profileResponseFromJson(String str) =>
    ProfileResponse.fromJson(json.decode(str));

String profileResponseToJson(ProfileResponse data) =>
    json.encode(data.toJson());

class ProfileResponse {
  int status;
  String message;
  Profile profile;

  ProfileResponse({
    required this.status,
    required this.message,
    required this.profile,
  });

  factory ProfileResponse.fromJson(Map<String, dynamic> json) =>
      ProfileResponse(
        status: json["status"],
        message: json["message"],
        profile: Profile.fromJson(json["result"]),
      );

  Map<String, dynamic> toJson() => {
        "status": status,
        "message": message,
        "result": profile.toJson(),
      };
}

class Profile {
  String id;
  String name;
  String username;
  String email;
  int balance;
  String address;
  DateTime createdAt;
  DateTime updatedAt;
  dynamic category;

  Profile({
    required this.id,
    required this.name,
    required this.username,
    required this.email,
    required this.balance,
    required this.address,
    required this.createdAt,
    required this.updatedAt,
    required this.category,
  });

  factory Profile.fromJson(Map<String, dynamic> json) => Profile(
        id: json["id"],
        name: json["name"],
        username: json["username"],
        email: json["email"],
        balance: json["balance"],
        address: json["address"],
        createdAt: DateTime.parse(json["createdAt"]),
        updatedAt: DateTime.parse(json["updatedAt"]),
        category: json["category"],
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "name": name,
        "username": username,
        "email": email,
        "balance": balance,
        "address": address,
        "createdAt": createdAt.toIso8601String(),
        "updatedAt": updatedAt.toIso8601String(),
        "category": category,
      };
}
