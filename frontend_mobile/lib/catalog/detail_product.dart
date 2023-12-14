import 'package:flutter/material.dart';
import 'package:frontend_mobile/models/catalog.dart';
import 'package:frontend_mobile/service/catalog_service.dart';
import 'package:frontend_mobile/widget/drawer.dart';
import 'dart:typed_data';
import 'package:intl/intl.dart';
import 'package:frontend_mobile/models/cart_response.dart';
import 'package:frontend_mobile/service/order_service.dart';
import 'package:frontend_mobile/order/cart_items_page.dart';

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
  int quantity = 1; // Initial quantity value
  // Access the OrderService instance directly from widget
  OrderService orderService = OrderService(baseUrl: 'https://apap-190.cs.ui.ac.id');

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
                          height: 200,
                          fit: BoxFit.cover,
                        );
                      } else if (imageSnapshot.hasError) {
                        return Text(
                            'Error loading image: ${imageSnapshot.error}');
                      } else {
                        return SizedBox(
                          width: double.infinity,
                          height: 200,
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
                  SizedBox(height: 8),
                  Text(
                    'Description: ${catalog.productDescription}',
                    style: TextStyle(
                      fontSize: 18,
                    ),
                  ),
                  SizedBox(height: 16),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        'Quantity:',
                        style: TextStyle(
                          fontSize: 18,
                        ),
                      ),
                      Row(
                        children: [
                          IconButton(
                            icon: Icon(Icons.remove),
                            onPressed: () {
                              if (quantity > 1) {
                                setState(() {
                                  quantity--;
                                });
                              } else {
                                null;
                              }
                            },
                          ),
                          Text(
                            '$quantity',
                            style: TextStyle(
                              fontSize: 18,
                            ),
                          ),
                          IconButton(
                            icon: Icon(Icons.add),
                            onPressed: () {
                              // Add logic to check against catalog.stok if needed
                              setState(() {
                                if (quantity < catalog.stok) {
                                  quantity++;
                                } else {
                                  print(
                                      'Quantity cannot exceed available stock (${catalog.stok}).');
                                }
                              });
                            },
                          ),
                          ElevatedButton(
                            onPressed: () async {
                              try {
                                // Fetch the catalog details
                                Result catalog = await widget.catalogService
                                    .getCatalogById(widget.catalogId);

                                Cart cart =
                                    await orderService.getCartByUserId();

                                // Create a CartItem instance with the selected quantity
                                CartItem cartItem = CartItem(
                                  id: cart.id,
                                  productId: catalog.id,
                                  quantity: quantity,
                                );

print("cart item id: "+cartItem.id);
print("cart product id: "+cartItem.productId);
print("cart item id: "+cartItem.id);
                                // Call the addCartItem method from OrderService
                                CartItem addedCartItem =
                                    await orderService.addCartItem(cartItem);

                                // Handle the result as needed
                                print(
                                    'CartItem added successfully: ${addedCartItem.toJson()}');
                              } catch (e) {
                                print('Error adding cart item: $e');
                                // Handle the error, show a snackbar, or display an error message to the user
                              }
                            },
                            child: Text('Add to Cart'),
                          ),
                        ],
                      ),
                    ],
                  ),
                  SizedBox(height: 16),
                  Center(
                    child: ElevatedButton(
                      onPressed: () {
                        Navigator.pushReplacement(
                          context,
                          MaterialPageRoute(
                            builder: (context) => CartItemsList(
                              catalogService: widget.catalogService,
                              orderService: orderService,
                            ),
                          ),
                        );
                      },
                      child: Text('Buy Now'),
                    ),
                  )
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
