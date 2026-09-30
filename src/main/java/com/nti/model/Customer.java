package com.nti.model;

import java.util.Set;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends Base {

    private String email;
    @Embedded
    private Address shippingAddress;

    @OneToMany(mappedBy = "customer")
    private Set<Order> orders;

}
