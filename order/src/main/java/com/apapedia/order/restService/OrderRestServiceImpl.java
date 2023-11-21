package com.apapedia.order.restService;

import com.apapedia.order.model.Order;
import com.apapedia.order.repository.OrderDb;
import com.apapedia.order.repository.OrderItemDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderRestServiceImpl implements OrderRestService{
    @Autowired
    private OrderDb orderDb;
    @Autowired
    private OrderItemDb orderItemDb;

    @Override
    public List<Order> getAllOrder() {return orderDb.findAll();}

    // Order Service 7: Get Order by customer_id
    @Override
    public List<Order> findOrderByCustomerId(UUID customer) {
        var getAllOrder = getAllOrder();
        List<Order> listOfOrder = new ArrayList<>();
        for (Order order : getAllOrder) {
            if(order.getCustomer().equals(customer)) {
                listOfOrder.add(order);
            }
        }
        System.out.println("Cart not found for ID: " + customer);
        return listOfOrder;
    }

    // Order Service 8: Get Order by seller_id
    @Override
    public List<Order> findOrderBySellerId(UUID seller) {
        var getAllOrder = getAllOrder();
        List<Order> listOfOrder = new ArrayList<>();
        for (Order order : getAllOrder) {
            if(order.getSeller().equals(seller)) {
                listOfOrder.add(order);
            }
        }
        System.out.println("Cart not found for ID: " + seller);

        return listOfOrder;
    }

    // Order Service 6: Post Order (customer)
    @Override
    public Order createRestOrder(Order order) {return orderDb.save(order);}
}
