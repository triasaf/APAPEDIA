import 'package:flutter/material.dart';
import 'package:frontend_mobile/models/catalog.dart';
import 'package:frontend_mobile/service/catalog_service.dart';
import 'package:frontend_mobile/widget/drawer.dart';
import 'dart:typed_data';

class CatalogListWidget extends StatefulWidget {
  final CatalogService catalogService;

  CatalogListWidget({required this.catalogService});

  @override
  _CatalogListWidgetState createState() => _CatalogListWidgetState();
}

// Import statement yang sesuai

class _CatalogListWidgetState extends State<CatalogListWidget> {
  late Future<List<Result>> catalogs;

  @override
  void initState() {
    super.initState();
    catalogs = widget.catalogService.getAllCatalogs();
  }

  Future<Uint8List> getImage(String catalogId) async {
    try {
      return await widget.catalogService.getImage(catalogId);
    } catch (e) {
      // Handle error
      return Uint8List(0);
    }
  }

  Future<Result> getCatalogById(String catalogId) async {
    try {
      return await widget.catalogService.getCatalogById(catalogId);
    } catch (e) {
      // Handle error
      throw Exception('Failed to load catalog by ID');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Catalog List'),
      ),
      drawer: const Drawers(),
      body: FutureBuilder<List<Result>>(
        future: catalogs,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return ListView.builder(
              itemCount: snapshot.data!.length,
              itemBuilder: (context, index) {
                var catalog = snapshot.data![index];
                return Card(
                  child: ListTile(
                    title: Text(catalog.productName),
                    subtitle: Text(catalog.productDescription),
                    leading: FutureBuilder<Uint8List>(
                      future: widget.catalogService.getImage(catalog.id),
                      builder: (context, imageSnapshot) {
                        if (imageSnapshot.connectionState ==
                                ConnectionState.done &&
                            imageSnapshot.hasData) {
                          return Image.memory(imageSnapshot.data!);
                        } else if (imageSnapshot.hasError) {
                          return Text(
                              'Error loading image: ${imageSnapshot.error}');
                        } else {
                          return CircularProgressIndicator();
                        }
                      },
                    ),
                  ),
                );
              },
            );
          } else if (snapshot.hasError) {
            return Center(
              child: Text('Error: ${snapshot.error}'),
            );
          } else {
            return Center(
              child: CircularProgressIndicator(),
            );
          }
        },
      ),
    );
  }
}
