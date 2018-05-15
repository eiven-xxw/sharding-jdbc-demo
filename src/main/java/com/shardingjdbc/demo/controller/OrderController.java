package com.shardingjdbc.demo.controller;

import com.shardingjdbc.demo.entity.Order;
import com.shardingjdbc.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping("/add")
    public Object add() {
       for (int i =10; i < 20; i++) {
            Order order = new Order();
            order.setUserId( i);
            order.setOrderId( i);
            orderRepository.save(order);
        }
        /*for (int i = 10; i < 20; i++) {
            Order order = new Order();
            order.setUserId( i + 1);
            order.setOrderId( i);
            orderRepository.save(order);
        }*/
        return "success";
    }

    @RequestMapping("query")
    private Object queryAll() {
        return "";
        //return orderRepository.findAll();
    }
}