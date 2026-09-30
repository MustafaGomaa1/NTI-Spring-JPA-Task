package com.nti.dto;

import com.nti.model.Customer;
import com.nti.model.Order;
import com.nti.model.OrderItem;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSummeryDTO {
    private Order order;
    private Customer customer;
    private List<OrderItem> orderItem;
    private BigDecimal totalPrice;
}
