// login.dart
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:frontend_mobile/service/auth_service.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:frontend_mobile/widget/drawer.dart';

class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController usernameController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  Future<void> _saveToken(String token) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', token);
  }

  Future<void> _saveUsername(String username) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('username', username);
  }

  Future<void> _saveName(String name) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('name', name);
  }

  Future<void> _saveUserId(String userId) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('userId', userId);
  }

  Future<void> _saveRole(String role) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('role', role);
  }

  Future<void> _login() async {
    final String? jwtToken = await loginUser(
      username: usernameController.text,
      password: passwordController.text,
    );

    print("login function");
    if (jwtToken != null) {
      // Decode JWT to get username
      final decodedToken = json.decode(
        ascii.decode(base64.decode(base64.normalize(jwtToken.split(".")[1]))),
      );

      // Extract username and timestamps
      String decodedUsername = decodedToken['sub'];
      String userId = decodedToken['userId'];
      String name = decodedToken['name'];
      String role = decodedToken['role'];
      final int? issuedAt = decodedToken['iat'];
      final int? expiration = decodedToken['exp'];

      // Check for null values
      if (decodedUsername != null && issuedAt != null && expiration != null) {
        // Convert timestamps to String
        final String issuedAtString = issuedAt.toString();
        final String expirationString = expiration.toString();

        // Debug
        print('Decoded Username: $decodedUsername');
        print('User ID: $userId');
        print('Name: $name');
        print('Role: $role');
        print('Issued At: $issuedAtString');
        print('Expiration: $expirationString');
      } else {
        print('Error: Username, Issued At, or Expiration is null');
      }

      print("Decoded JWT Payload: $decodedToken");

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Username $decodedUsername successfully logged in!'),
          duration: const Duration(seconds: 2),
        ),
      );

      // Store token in shared preferences
      await _saveToken(jwtToken);
      await _saveUsername(decodedUsername);
      await _saveUserId(userId);
      await _saveRole(role);
      await _saveName(name);

      // Debug
      SharedPreferences prefs = await SharedPreferences.getInstance();
      print("this is from shared preferences:");
      print(prefs.getString('userId'));
      print(prefs.getString('name'));
      print(prefs.getString('username'));
      print(prefs.getString('role'));
      print(prefs.getString('token'));

      // Navigate to the homepage
      Navigator.pushReplacementNamed(
          context, '/'); // Replace '/home' with your actual homepage route
    } else {
      // Handle login failure, you may show an error message
      print("Login failed");
    }
  }

  void _goToRegisterPage() {
    Navigator.pushNamed(context, '/register'); // Replace '/register' with your register page route
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Login'),
      ),
      drawer: const Drawers(),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextField(
              controller: usernameController,
              decoration: InputDecoration(labelText: 'Username'),
            ),
            TextField(
              controller: passwordController,
              decoration: InputDecoration(labelText: 'Password'),
              obscureText: true,
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _login,
              child: Text('Login'),
            ),
            SizedBox(height: 10),
            TextButton(
              onPressed: _goToRegisterPage,
              child: Text("Don't have an account? Register here"),
            ),
          ],
        ),
      ),
    );
  }
}
