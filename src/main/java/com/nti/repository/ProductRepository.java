package com.nti.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nti.model.Order;
import org.springframework.stereotype.Repository;

import com.nti.model.Category;
import com.nti.model.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Product product) {
        if (entityManager.find(Product.class, product.getId()) == null)
            entityManager.persist(product);
        else
            entityManager.merge(product);
    }

    public Optional<Product> findById(Number id) {
        return Optional.of(entityManager.find(Product.class, id));
    }

    public Product findBySku(String sku) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.sku =: sku", Product.class)
                .setParameter("sku", sku).getSingleResult();
    }

    public List<Product> search(String keyword, BigDecimal minPrice, BigDecimal maxPrice, String category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> product = cq.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            predicates.add(cb.like(cb.lower(product.get("name")),
                    "%" + keyword.toLowerCase() + "%"));
        }
        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(product.get("price"), maxPrice));
        }
        if (category != null && !category.isBlank()) {
            Join<Product, Category> join = product.join("category", JoinType.INNER);
            predicates.add(cb.equal(join.get("category"), category));
        }

        cq.select(product).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Product> findLowStock(int threshold) {
        return entityManager.createQuery("SELECT p FROM Product p ORDER BY p.stock DESC LIMIT :threshold ").setParameter("threshold",threshold).setMaxResults(threshold).getResultList();
    }

    public List<Product> findPage(int pageNumber, int pageSize) {
        return entityManager.createQuery("SELECT p FROM Product p ORDER BY p.createAt OFFSET :pageNumber LIMIT :pageSize").setParameter("pageNumber",pageNumber).setParameter("pageSize",pageSize).setParameter("pageNumber",(pageNumber-1)*pageSize).getResultList();
    }
}