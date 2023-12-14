import 'package:flutter/material.dart';
import 'package:frontend_mobile/models/cart_response.dart';
import 'package:frontend_mobile/models/catalog.dart';
import 'package:frontend_mobile/service/catalog_service.dart';
import 'package:frontend_mobile/service/order_service.dart';
import 'package:frontend_mobile/widget/drawer.dart';

class CartItemsList extends StatefulWidget {
  final OrderService orderService;
  final CatalogService catalogService;

  const CartItemsList(
      {super.key, required this.orderService, required this.catalogService});

  @override
  State<CartItemsList> createState() => _CartItemsListState();
}

// Import statement yang sesuai

class _CartItemsListState extends State<CartItemsList> {
  late Future<Cart> cart;
  late Future<List<Result>> catalogs;

  @override
  void initState() {
    super.initState();
    cart = widget.orderService.getCartByUserId();
    catalogs = widget.catalogService.getAllCatalogs();
  }

  Future<void> updateQuantityAndTotalPrice(CartItem cartItem) async {
    try {
      await widget.orderService.updateCartItemQuantity(cartItem);

      setState(() {});
    } catch (e) {
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

      setState(() {});

      setState(() {});

      return response;
    } catch (e) {
      throw Exception('Failed to delete cart item: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Cart Page'),
      ),
      drawer: const Drawers(),
      body: FutureBuilder<Cart>(
        future: cart,
        builder: (context, cartSnapshot) {
          if (cartSnapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (cartSnapshot.hasError) {
            return Center(child: Text('Error: ${cartSnapshot.error}'));
          } else if (!cartSnapshot.hasData) {
            return const Center(child: Text('No data available'));
          }

          var cart = cartSnapshot.data!;

          return ListView.builder(
            itemCount: cart.listCartItem.length,
            itemBuilder: (context, index) {
              var cartItem = cart.listCartItem[index];

              return FutureBuilder<Result>(
                future: getCatalogById(cartItem.productId),
                builder: (context, catalogSnapshot) {
                  if (catalogSnapshot.connectionState ==
                      ConnectionState.waiting) {
                    return const CircularProgressIndicator();
                  } else if (catalogSnapshot.hasError) {
                    return Text('Error: ${catalogSnapshot.error}');
                  } else if (!catalogSnapshot.hasData) {
                    return const Text('No data available');
                  }

                  var catalog = catalogSnapshot.data!;

                  return Card(
                    child: ListTile(
                      title: Text(catalog.productName),
                      subtitle: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                              'Price: \$${catalog.price.toStringAsFixed(2)}'), // Assuming productPrice is a double
                          Row(
                            children: [
                              ElevatedButton(
                                onPressed: () async {
                                  cartItem.quantity = cartItem.quantity + 1;
                                  await updateQuantityAndTotalPrice(cartItem);
                                },
                                child: const Icon(Icons.add),
                              ),
                              const SizedBox(
                                  width: 8), // Adjust spacing as needed
                              Text('Quantity: ${cartItem.quantity}'),
                              const SizedBox(width: 8),
                              ElevatedButton(
                                onPressed: () async {
                                  cartItem.quantity = cartItem.quantity - 1;
                                  await updateQuantityAndTotalPrice(cartItem);
                                },
                                child: const Icon(Icons.remove),
                              ),
                            ],
                          ),
                          Text(
                              'Total Price: \$${(catalog.price * cartItem.quantity).toStringAsFixed(2)}'),
                        ],
                      ),
                      trailing: ElevatedButton(
                        onPressed: () async {
                          // TODO: Handle delete
                          await deleteCartItem(cartItem);
                        },
                        child: const Icon(Icons.delete),
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
