import 'package:flutter/material.dart';
import 'package:frontend_mobile/models/cart_response.dart';
import 'package:frontend_mobile/models/catalog.dart';
import 'package:frontend_mobile/service/catalog_service.dart';
import 'package:frontend_mobile/service/order_service.dart';
import 'package:frontend_mobile/widget/drawer.dart';
import 'dart:typed_data';

class CartItemsList extends StatefulWidget {
  final OrderService orderService;
  final CatalogService catalogService;

  CartItemsList({
    required this.orderService,
    required this.catalogService
    });

  @override
  _CartItemsListState createState() => _CartItemsListState();
}

// Import statement yang sesuai

class _CartItemsListState extends State<CartItemsList> {
  late Future<Cart> cart;
  late Future<List<Result>> catalogs;

  @override
  void initState() {
    super.initState();
    //TODO: UBAH BIAR AMBIL DARI TOKEN
    //TODO: UBAH BIAR AMBIL DARI TOKEN
    //TODO: UBAH BIAR AMBIL DARI TOKEN
    //TODO: UBAH BIAR AMBIL DARI TOKEN
    cart = widget.orderService.getCartByUserId(); 
    catalogs = widget.catalogService.getAllCatalogs();
  }

    Future<void> updateQuantityAndTotalPrice(CartItem cartItem) async {
    try {
      // Update the quantity on the server
      await widget.orderService.updateCartItemQuantity(cartItem);

      // Trigger a rebuild of the widget to update UI
      setState(() {
        // Nothing specific needs to be done here since the UI will be updated in the build method
      });
    } catch (e) {
      // Handle error
      print('Failed to update cart item quantity: $e');
      throw Exception('Failed to update cart item quantity: $e');
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

    Future<String> deleteCartItem(CartItem cartItem) async {
      try {
        final response = await widget.orderService.deleteCartItem(cartItem);

        setState(() {
          
        });

        setState(() {
          
        });

        return response;
        
      } catch (e) {
        print('Failed to delete cart item: $e');
        throw Exception('Failed to delete cart item: $e');
      }
    }

@override
Widget build(BuildContext context) {
  return Scaffold(
    appBar: AppBar(
      title: Text('Cart Page'),
    ),
    drawer: const Drawers(),
    body: FutureBuilder<Cart>(
      future: cart,
      builder: (context, cartSnapshot) {
        if (cartSnapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else if (cartSnapshot.hasError) {
          return Center(child: Text('Error: ${cartSnapshot.error}'));
        } else if (!cartSnapshot.hasData) {
          return Center(child: Text('No data available'));
        }

        var cart = cartSnapshot.data!;

        return ListView.builder(
          itemCount: cart.listCartItem.length,
          itemBuilder: (context, index) {
            var cartItem = cart.listCartItem[index];

            return FutureBuilder<Result>(
              future: getCatalogById(cartItem.productId),
              builder: (context, catalogSnapshot) {
                if (catalogSnapshot.connectionState == ConnectionState.waiting) {
                  return CircularProgressIndicator();
                } else if (catalogSnapshot.hasError) {
                  return Text('Error: ${catalogSnapshot.error}');
                } else if (!catalogSnapshot.hasData) {
                  return Text('No data available');
                }

                var catalog = catalogSnapshot.data!;

                return Card(
                  child: ListTile(
                    title: Text(catalog.productName),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('Price: \$${catalog.price.toStringAsFixed(2)}'), // Assuming productPrice is a double
                        Row(
                          children: [
                            ElevatedButton(
                                onPressed: () async {
                                  cartItem.quantity = cartItem.quantity + 1;
                                  await updateQuantityAndTotalPrice(cartItem);
                                },
                                child: Icon(Icons.add),
                              ),
                            SizedBox(width: 8), // Adjust spacing as needed
                            Text('Quantity: ${cartItem.quantity}'),
                            SizedBox(width: 8),
                            ElevatedButton(
                                onPressed: () async {
                                  cartItem.quantity = cartItem.quantity - 1;
                                  await updateQuantityAndTotalPrice(cartItem);
                                },
                                child: Icon(Icons.remove),
                              ),
                          ],
                        ),
                        Text('Total Price: \$${(catalog.price * cartItem.quantity).toStringAsFixed(2)}'),
                      ],
                    ),
                    trailing: ElevatedButton(
                      onPressed: () async {
                        // TODO: Handle delete
                        await deleteCartItem(cartItem);
                      },
                      child: Icon(Icons.delete),
                    ),
                  ),
                );
              },
            );
          },
        );
      },
    ),
  );
}
}