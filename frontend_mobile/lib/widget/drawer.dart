import 'package:flutter/material.dart';
import 'package:frontend_mobile/auth/login.dart';
import 'package:frontend_mobile/auth/profile.dart';
import 'package:frontend_mobile/catalog/all_product.dart';
import 'package:frontend_mobile/order/cart_items_page.dart';
import 'package:frontend_mobile/order/order_history_page.dart';
import 'package:frontend_mobile/service/order_service.dart';
import 'package:frontend_mobile/service/profile_service.dart';
import '../main.dart';
import 'package:frontend_mobile/service/catalog_service.dart';

class Drawers extends StatefulWidget {
  const Drawers({super.key});

  @override
  State<Drawers> createState() => _DrawerState();
}

class _DrawerState extends State<Drawers> {
  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: Padding(
        padding: const EdgeInsets.all(2),
        child: Column(
          children: [
            ListTile(
              title: const Text("Home Page"),
              onTap: () {
                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const MyHomePage(
                    ),
                  ),
                );
              },
            ),
            ListTile(
              title: const Text("Catalog Page"),
              onTap: () async {
                // Create an instance of CatalogService with the appropriate URL
                CatalogService catalogService =
                    CatalogService(baseUrl: 'https://apap-189.cs.ui.ac.id');

                // Use the CatalogService instance when calling Navigator.pushReplacement
                await Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) =>
                        CatalogListWidget(catalogService: catalogService),
                  ),
                );
              },
            ),
            ListTile(
              title: const Text("Profile Page"),
              onTap: ()  async {
                ProfileService profileService = ProfileService(baseUrl: 'https://apap-188.cs.ui.ac.id');

                await Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) =>
                        ProfileScreen(profileService: profileService),
                  ),
                );
              },
            ),
            ListTile(
              title: const Text("Login Page"),
              onTap: () {
                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(builder: (context) => LoginPage()),
                );
              },
            ),
            ListTile(
              title: const Text("Cart Page"),
              onTap: () async {
                // Create instances of OrderService and CatalogService with the appropriate URLs
                OrderService orderService = OrderService(baseUrl: 'https://apap-190.cs.ui.ac.id');
                CatalogService catalogService = CatalogService(baseUrl: 'https://apap-189.cs.ui.ac.id');

                await Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) =>
                        CartItemsList(catalogService: catalogService, orderService: orderService), // TODO: CHANGE TO CART WIDGET
                  ),
                );
              },
            ),
            ListTile(
            title: const Text("Order History Page"),
            onTap: () async {
              // Buat instance dari CatalogService dengan URL yang sesuai
              OrderService orderService =
                  OrderService(baseUrl: 'https://apap-190.cs.ui.ac.id');

              // Gunakan instance CatalogService saat memanggil Navigator.pushReplacement
              await Navigator.pushReplacement(
                context,
                MaterialPageRoute(
                    builder: (context) => 
                        OrderItemList(orderService: orderService)),
              );
            },
          ),
          ],
        ),
      ),
    );
  }
}

