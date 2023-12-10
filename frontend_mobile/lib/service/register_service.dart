// register_functions.dart
import 'dart:convert';
import 'package:http/http.dart' as http;

Future<bool> registerUser({
  required String name,
  required String username,
  required String password,
  required String email,
  required String address,
}) async {
  final String url = "http://10.0.2.2:8082/api/register"; // Replace with your API endpoint

  final Map<String, String> data = {
    "name": name,
    "username": username,
    "password": password,
    "email": email,
    "address": address,
    "role": "CUSTOMER",
  };

  final response = await http.post(
    Uri.parse(url),
    headers: {'Content-Type': 'application/json'},
    body: jsonEncode(data),
  );

  if (response.statusCode == 200) {
    // Registration successful
    print("Registration successful");
    return true;
  } else {
    // Registration failed
    print("Registration failed: ${response.body}");
    return false;
  }
}
