import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:frontend_mobile/auth/login.dart';
import 'package:frontend_mobile/models/profile_response.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

class ProfileService {
  final String baseUrl;

  ProfileService({required this.baseUrl});

  Future<ProfileResponse> getProfile(BuildContext context) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String token = prefs.getString('token') ?? '';

    void redirectToLoginPage() {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => LoginPage()),
      );
    }

    if (token.isEmpty) {
      redirectToLoginPage();
    }

    Map<String, String> headers = {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };

    final response = await http.get(
      Uri.parse('$baseUrl/api/me'),
      headers: headers,
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body);
      return ProfileResponse.fromJson(data);
    } else {
      throw Exception('Failed to load profile information');
    }
  }
}
