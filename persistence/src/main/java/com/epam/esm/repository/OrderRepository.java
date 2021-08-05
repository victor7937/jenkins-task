package com.epam.esm.repository;

import com.epam.esm.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Repository for manipulating orders data in database
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Gets a page of users orders by params
     * @param userId - id of a user
     * @param minCost - minimal order cost
     * @param maxCost - maximal order cost
     * @param minTime - minimal time of purchase
     * @param maxTime - maximal time of purchase
     * @param pageable - contains info about page size, current page and sorting params
     * @return Page of users orders
     */
    Page<Order> getOrdersByUser_IdAndCostBetweenAndTimeOfPurchaseBetween(Long userId, Float minCost, Float maxCost,
                                                                         LocalDateTime minTime, LocalDateTime maxTime, Pageable pageable);

    /**
     * Gets order of a user by its id
     * @param userId id of an order
     * @param orderId id of a user
     * @return optional that contains an order or empty Optional if it wasn't found
     */
    Optional<Order> findOrderByUser_IdAndId(Long userId, Long orderId);
}
