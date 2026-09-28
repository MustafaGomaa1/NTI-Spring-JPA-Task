package com.nti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nti.model.Customer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class CustomerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Customer customer) {
        if (entityManager.find(Customer.class, customer.getId()) == null) {
            entityManager.persist(customer);
        }
        entityManager.merge(customer);
    }

    public Optional<Customer> findById(Number id) {
        Customer customer = entityManager.find(Customer.class, id);
        return Optional.of(customer);
    }

    public Optional<Customer> findByEmail(String email) {
        Customer customer = entityManager.createQuery("SELECT c From Customer c WHERE c.email =:email", Customer.class)
                .setParameter("email", email).getSingleResult();
        return Optional.of(customer);
    }

    public List<Customer> findAll() {
        return entityManager.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }
}
