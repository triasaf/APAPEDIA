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
  const String url = "https://apap-188.cs.ui.ac.id/api/register";

  final Map<String, String> data = {
    "name": name,
    "username": username,
    "password": password,
    "email": email,
    "address": address,
    "role": "CUSTOMER",
  };

  try {
    final response = await http.post(
      Uri.parse(url),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(data),
    );

    if (response.statusCode == 200) {
      // Registration successful
      return true;
    } else {
      // Registration failed
      return false;
    }
  } catch (error) {
    return false;
  }
}
