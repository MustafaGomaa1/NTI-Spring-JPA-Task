package com.nti.service;

import com.nti.exception.CustomerException;
import com.nti.exception.DuplicateCustomerException;
import com.nti.model.Customer;
import com.nti.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public void register(Customer customer) {
        if(customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new DuplicateCustomerException("Customer already exists");
        }
        customerRepository.save(customer);
    }
}
