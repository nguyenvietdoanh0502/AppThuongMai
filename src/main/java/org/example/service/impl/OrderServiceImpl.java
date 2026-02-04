package org.example.service.impl;

import org.example.dao.OrderDAO;
import org.example.model.Order;
import org.example.service.OrderService;

public class OrderServiceImpl implements OrderService {
    OrderDAO orderDAO = new OrderDAO();
    @Override
    public void addOrder(Order order) {
        orderDAO.addOrder(order);
    }
}
