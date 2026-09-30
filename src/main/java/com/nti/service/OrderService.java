package com.nti.service;

import com.nti.dto.OrderSummeryDTO;
import com.nti.exception.*;
import com.nti.model.*;
import com.nti.repository.CustomerRepository;
import com.nti.repository.OrderRepository;
import com.nti.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;


    @Transactional
    public void placeOrder(int customerId, Map<Long,Integer> productQuantity){
        Order order = new Order();
        Customer customer = customerRepository.findById(customerId).orElseThrow(()->new CustomerException("Customer Not Found"));
        List<OrderItem> orderProducts = new ArrayList<>();
        for (Map.Entry<Long,Integer> entry : productQuantity.entrySet()){
            Product product = productRepository.findById(entry.getKey()).orElseThrow(()->new ProductException("Product Not Found"));
            if(product.getStock()<entry.getValue()){
                throw new InsufficientStockException("Insufficient Stock");
            }
            product.setStock(product.getStock()-entry.getValue());
            productRepository.save(product);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(entry.getValue());
            orderItem.setUnitPrice(product.getPrice());
            orderProducts.add(orderItem);
        }
        order.setCustomer(customer);
        order.setItems(orderProducts);
        order.setStatus(Status.NEW);
        order.setOrderAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    public void pay(int orderId,Payment payment){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException("Order Not Found"));
        if(!order.getStatus().equals(Status.NEW)){
            throw new InvalidOrderStateException("Order Status Not New");
        }
        order.setPayment(payment);
        order.setStatus(Status.PAID);
//        orderRepository.save(order);
    }

    public void ship(int orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException("Order Not Found"));
        if(!order.getStatus().equals(Status.PAID)){
            throw new InvalidOrderStateException("Order Status Not Paid");
        }
        order.setStatus(Status.SHIPPED);
        orderRepository.save(order);
    }

    @Transactional
    public void cancel(int orderId){
        Order order =  orderRepository.findById(orderId).orElseThrow(()->new OrderException("Order Not Found"));
        if(order.getStatus().equals(Status.PAID) || order.getStatus().equals(Status.NEW)){
            order.setStatus(Status.CANCELLED);
            for (OrderItem orderItem : order.getItems()) {
                Product product = orderItem.getProduct();
                product.setStock(product.getStock()+orderItem.getQuantity());
                productRepository.save(product);
            }
        }
        else
            throw new InvalidOrderStateException("Order Can't Cancel In This Point");
    }

    @Transactional(readOnly = true)
    public OrderSummeryDTO getOrderSummery(int orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException("Order Not Found"));
        BigDecimal totalPrice = new BigDecimal(0);
        for (OrderItem orderItem : order.getItems()) {
            BigDecimal unitTotalPrice = new BigDecimal(0);
            unitTotalPrice =(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            totalPrice.add(unitTotalPrice);
        }
        return OrderSummeryDTO.builder()
                .order(order)
                .customer(order.getCustomer())
                .orderItem(order.getItems())
                .totalPrice(totalPrice)
                .build();
    }
}
