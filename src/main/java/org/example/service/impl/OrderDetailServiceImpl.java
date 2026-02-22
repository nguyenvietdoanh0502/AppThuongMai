package org.example.service.impl;

import org.example.dao.OrderDetailDAO;
import org.example.model.OrderDetail;
import org.example.service.OrderDetailService;

public class OrderDetailServiceImpl implements OrderDetailService {
    OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    @Override
    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetailDAO.addOrderDetail(orderDetail);
    }
}
