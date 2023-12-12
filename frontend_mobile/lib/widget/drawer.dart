import 'package:flutter/material.dart';
import 'package:frontend_mobile/auth/login.dart';
import 'package:frontend_mobile/auth/register.dart';
import 'package:frontend_mobile/catalog/all_product.dart';
import 'package:frontend_mobile/order/cart_items_page.dart';
import 'package:frontend_mobile/service/order_service.dart';
import '../main.dart';
import 'package:frontend_mobile/service/catalog_service.dart';

import '../main.dart';

class Drawers extends StatefulWidget {
  const Drawers({Key? key}) : super(key: key);

  @override
  _DrawerState createState() => _DrawerState();
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
                      title: 'APAPEDIA',
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
                    CatalogService(baseUrl: 'http://localhost:8081');

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
                OrderService orderService = OrderService(baseUrl: 'http://localhost:8080');
                CatalogService catalogService = CatalogService(baseUrl: 'http://localhost:8081');

                await Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) =>
                        CartItemsList(catalogService: catalogService, orderService: orderService), // TODO: CHANGE TO CART WIDGET
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}

