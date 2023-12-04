import 'package:flutter/material.dart';
import 'package:frontend_mobile/catalog/all_product.dart';
import '../main.dart';
import 'package:frontend_mobile/service/catalog_service.dart';

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
                        )),
              );
            },
          ),
          ListTile(
            title: const Text("Catalog Page"),
            onTap: () async {
              // Buat instance dari CatalogService dengan URL yang sesuai
              CatalogService catalogService =
                  CatalogService(baseUrl: 'http://localhost:8081');

              // Gunakan instance CatalogService saat memanggil Navigator.pushReplacement
              await Navigator.pushReplacement(
                context,
                MaterialPageRoute(
                    builder: (context) =>
                        CatalogListWidget(catalogService: catalogService)),
              );
            },
          ),
        ],
      ),
    ));
  }
}
