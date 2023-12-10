package com.apapedia.order.restService;

import com.apapedia.order.dto.request.UpdateOrderRequestDTO;
import com.apapedia.order.dto.response.SalesDTO;
import com.apapedia.order.model.Order;
import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.OrderDb;
import com.apapedia.order.repository.OrderItemDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderRestServiceImpl implements OrderRestService {
    @Autowired
    private OrderDb orderDb;
    @Autowired
    private OrderItemDb orderItemDb;
    @Autowired
    private CartDb cartDb;

    @Override
    public List<Order> getAllOrder() {
        return orderDb.findAll();
    }

    @Override
    public Order findOrderById(UUID orderId) {
        for (Order order: getAllOrder()) {
            if(order.getId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    // Order Service 7: Get Order by customer_id
    @Override
    public List<Order> findOrderByCustomerId(UUID customer) {
        var getAllOrder = getAllOrder();
        List<Order> listOfOrder = new ArrayList<>();
        for (Order order : getAllOrder) {
            if (order.getCustomer().equals(customer)) {
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
            if (order.getSeller().equals(seller)) {
                listOfOrder.add(order);
            }
        }
        System.out.println("Cart not found for ID: " + seller);

        return listOfOrder;
    }

    // Order Service 6: Post Order (customer)
    @Override
    public Order createRestOrder(Order order) {
        return orderDb.save(order);
    }

    @Override
    public List<SalesDTO> getDailySalesBySellerId(UUID sellerId) {
        List<Object[]> dailySalesData = orderDb.getDailySalesDataBySellerId(sellerId);

        // Mapping data dari daftar objek menjadi list SalesDTO
        List<SalesDTO> dailySalesList = new ArrayList<>();
        for (Object[] data : dailySalesData) {
            // Mengambil nilai hari dari hasil query
            Integer day = (Integer) data[0];

            // Membuat objek Calendar dan menetapkan tanggal bulan dan tahun tetap
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

            // Konversi ke Date dan tambahkan ke list
            Date date = calendar.getTime();
            Long numberOfProductsSold = (Long) data[1];

            // Konversi ke SalesDTO dan tambahkan ke list
            SalesDTO salesDTO = new SalesDTO(date, numberOfProductsSold.intValue());
            dailySalesList.add(salesDTO);
        }
        return dailySalesList;
    }

    public Order changeStatusOrder(UpdateOrderRequestDTO orderDTO) {
        Order order = findOrderById(orderDTO.getId());
        order.setStatus(orderDTO.getStatus());
        orderDb.save(order);

        return order;
    }
}
