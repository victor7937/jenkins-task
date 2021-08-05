package com.epam.esm.service;

import com.epam.esm.criteria.OrderCriteria;
import com.epam.esm.dto.OrderDTO;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.Order;
import com.epam.esm.exception.IncorrectDataServiceException;
import com.epam.esm.exception.NotFoundServiceException;


/**
 * Service for manipulating orders data
 */
public interface OrderService {

    /**
     * Create a new order of user for gift certificate
     * @param orderDTO - dto that contains users email and certificates id
     * @return created order
     * @throws IncorrectDataServiceException if dto data is incorrect
     */
    Order makeOrder(OrderDTO orderDTO);

    /**
     * Gets page of orders of a user
     * @param criteria - criteria with params for filtering and sorting
     * @param pageSize - size of one page
     * @param pageNumber - number of a current page
     * @param userId - id of user for getting its orders
     * @return page with orders found
     * @throws IncorrectDataServiceException if criteria or pagination params are incorrect
     * @throws NotFoundServiceException if user wasn't found
     */
    PagedDTO<Order> getOrdersOfUser(Long userId, OrderCriteria criteria, int pageSize, int pageNumber);

    /**
     * Get order of a user by its id
     * @param userId id of a user
     * @param orderId id of users order
     * @return order found
     * @throws IncorrectDataServiceException if email or id are incorrect
     * @throws NotFoundServiceException if order wasn't found
     */
    Order getOrder(Long userId, Long orderId);
}
