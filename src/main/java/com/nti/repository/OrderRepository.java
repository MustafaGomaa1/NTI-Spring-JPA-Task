package com.nti.repository;

import com.nti.model.Order;
import com.nti.model.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Order order) {
        if (order.getId() == 0)
            em.persist(order);
        else
            em.merge(order);
    }

    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o ORDER BY o.id", Order.class).getResultList();
    }

    public Optional<Order> findById(int id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    public Order findByIdWithItems(int id) {
        return  em.createQuery("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id =:id",Order.class).setParameter("id",id).getSingleResult();
    }

    public List<Order> findByCustomerId(int customerId) {
        return em.createQuery("SELECT o FROM Order o WHERE o.customer.id = :id",Order.class).setParameter("id",customerId).getResultList();
    }

    public List<Order> findByStatus(Status status) {
        return em.createQuery("SELECT o FROM Order o WHERE o.status = :status",Order.class).setParameter("status",status).getResultList();
    }
}
