import 'package:flutter/material.dart';
import 'package:frontend_mobile/models/order_response.dart';
import 'package:frontend_mobile/service/order_service.dart';
import 'package:frontend_mobile/widget/drawer.dart';

class OrderItemList extends StatefulWidget {
  final OrderService orderService;

  const OrderItemList({super.key,
    required this.orderService,
    });

  @override
  State<OrderItemList> createState() => _OrderItemListState();
}

// Import statement yang sesuai

class _OrderItemListState extends State<OrderItemList> {
  late Future<OrderResponse> orderResponse;

  @override
  void initState() {
    super.initState();
    orderResponse = widget.orderService.getOrderByCustomerId();
  }

  Future<void> updateOrderStatus(Order order) async {
    try {
      // Update the order status on the server
      await widget.orderService.updateOrderStatus(order);

      // Trigger a rebuild of the widget to update UI
      setState(() {
        // Nothing specific needs to be done here since the UI will be updated in the build method
      });
    } catch (e) {
      throw Exception('Failed to update order status');
    }
  }

@override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Order History Page'),
      ),
      drawer: const Drawers(),
      body: FutureBuilder<OrderResponse>(
        future: orderResponse,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData) {
            return const Center(child: Text('No data available'));
          }

          var orderResponse = snapshot.data!;

          return ListView.builder(
            itemCount: orderResponse.order.length,
            itemBuilder: (context, index) {
              var order = orderResponse.order[index];
              return Card(
                child: ListTile(
                  title: Text(order.seller), // Replace with the correct property
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Order Date: ${order.createdAt}'),
                      Text('Order Status: ${order.status}'),
                      Text('Total Price: ${order.totalPrice}'),
                    ],
                  ),
                  trailing: ElevatedButton(
                    onPressed: () async {
                                  order.status = order.status + 1;
                                  await updateOrderStatus(order);
                                },
                    child: const Text('Update Status'),
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}