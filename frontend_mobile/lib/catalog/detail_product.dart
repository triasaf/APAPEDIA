import 'package:flutter/material.dart';
import 'package:frontend_mobile/models/catalog.dart';
import 'package:frontend_mobile/service/catalog_service.dart';
import 'package:frontend_mobile/widget/drawer.dart';
import 'dart:typed_data';
import 'package:intl/intl.dart';

class CatalogDetailWidget extends StatefulWidget {
  final CatalogService catalogService;
  final String catalogId;

  CatalogDetailWidget({required this.catalogService, required this.catalogId});

  @override
  _CatalogDetailWidgetState createState() => _CatalogDetailWidgetState();
}

class _CatalogDetailWidgetState extends State<CatalogDetailWidget> {
  late Future<Result> catalog;
  late Future<Uint8List> image;

  @override
  void initState() {
    super.initState();
    catalog = widget.catalogService.getCatalogById(widget.catalogId);
    image = widget.catalogService.getImage(widget.catalogId);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Catalog Detail'),
        backgroundColor: Colors.blue,
        // leading: IconButton(
        //   icon: Icon(Icons.arrow_back),
        //   onPressed: () => Navigator.pop(context),
        // ),
      ),
      drawer: const Drawers(),
      body: FutureBuilder<Result>(
        future: catalog,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.done &&
              snapshot.hasData) {
            var catalog = snapshot.data!;
            final currencyFormatter = NumberFormat.currency(
              locale: 'id_ID',
              symbol: 'Rp',
              decimalDigits: 0,
            );

            return Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  FutureBuilder<Uint8List>(
                    future: widget.catalogService.getImage(catalog.id),
                    builder: (context, imageSnapshot) {
                      if (imageSnapshot.connectionState ==
                              ConnectionState.done &&
                          imageSnapshot.hasData) {
                        return Image.memory(
                          imageSnapshot.data!,
                          width: double.infinity,
                          height: 200, // Adjust the height as needed
                          fit: BoxFit.cover,
                        );
                      } else if (imageSnapshot.hasError) {
                        return Text(
                            'Error loading image: ${imageSnapshot.error}');
                      } else {
                        return SizedBox(
                          width: double.infinity,
                          height: 200, // Adjust the height as needed
                          child: Center(child: CircularProgressIndicator()),
                        );
                      }
                    },
                  ),
                  SizedBox(height: 16),
                  Center(
                    child: Text(
                      catalog.productName,
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 24,
                      ),
                    ),
                  ),
                  SizedBox(height: 8),
                  Text(
                    'Price: ${currencyFormatter.format(catalog.price)}',
                    style: TextStyle(
                      fontSize: 18,
                    ),
                  ),
                  Text(
                    'Stock: ${catalog.stok}',
                    style: TextStyle(
                      fontSize: 18,
                    ),
                  ),
                  SizedBox(height: 16),
                  Text(
                    'Description: ${catalog.productDescription}',
                    style: TextStyle(
                      fontSize: 18,
                    ),
                  ),
                ],
              ),
            );
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else {
            return Center(child: CircularProgressIndicator());
          }
        },
      ),
    );
  }
}
