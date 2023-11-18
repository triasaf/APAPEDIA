package com.apapedia.order.restService;

import com.apapedia.order.model.Cart;
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
    public Order findOrderByCustomerId(UUID customer) {
        try {
            for (Order order : getAllOrder()) {
                if(order.getCustomer().equals(customer)) {
                    return order;
                }
            }
            System.out.println("Cart not found for ID: " + customer);
            throw new Exception("Cart not found for ID: " + customer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
}
