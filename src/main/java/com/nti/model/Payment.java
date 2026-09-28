package com.nti.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Payment extends Base {
    @OneToOne
    @JoinColumn(name = "order_payment")
    private Order order;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Method method;
    private LocalDateTime payedAt;

}
