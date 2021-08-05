package com.epam.esm.service.impl;

import com.epam.esm.criteria.OrderCriteria;
import com.epam.esm.criteria.SortingOrder;
import com.epam.esm.dto.OrderDTO;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.exception.*;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.OrderService;
import com.epam.esm.validator.CriteriaValidator;
import com.epam.esm.validator.ServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Repository
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_NOT_EXIST_MSG = "Order with such params is not exist";
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final GiftCertificateRepository giftCertificateRepository;

    private final CriteriaValidator<OrderCriteria> criteriaValidator;

    private final ServiceValidator<OrderDTO> validator;

    private static final String USERS_ID_NOT_VALID = "Users id is not valid";
    private static final String USERS_EMAIL_NOT_EXISTS_MSG = "User with email %s doesn't exist";
    private static final String USER_NOT_EXIST_MSG = "User with id %s doesn't exist";
    private static final String CERTIFICATE_NOT_EXIST_MSG = "Certificate with id %s doesn't exist";
    private static final String INCORRECT_ORDER_MSG = "Incorrect id of an order or a user";
    private static final String INCORRECT_PARAMS_MSG = "Incorrect request parameter values";
    private static final String INCORRECT_ORDER_PARAMS_MSG = "Incorrect order details";

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, GiftCertificateRepository giftCertificateRepository,
                            CriteriaValidator<OrderCriteria> criteriaValidator, ServiceValidator<OrderDTO> validator) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.giftCertificateRepository = giftCertificateRepository;
        this.criteriaValidator = criteriaValidator;
        this.validator = validator;
    }

    @Override
    @Transactional
    public Order makeOrder(OrderDTO orderDTO){
        if (!validator.validate(orderDTO)){
            throw new IncorrectDataServiceException(INCORRECT_ORDER_PARAMS_MSG);
        }

        User user = userRepository.findByEmail(orderDTO.getEmail()).orElseThrow(() -> new NotFoundServiceException(
                String.format(USERS_EMAIL_NOT_EXISTS_MSG, orderDTO.getEmail())));

        GiftCertificate certificate = giftCertificateRepository.findByIdAndDeletedIsFalse(orderDTO.getId()).orElseThrow(() ->
                new NotFoundServiceException(String.format(CERTIFICATE_NOT_EXIST_MSG, orderDTO.getId())));

        Order order = new Order();
        order.setGiftCertificate(certificate);
        order.setCost(certificate.getPrice());
        user.addOrder(order);
        orderRepository.save(order);

        return order;
    }

    @Override
    @Transactional()
    public PagedDTO<Order> getOrdersOfUser(Long userId, OrderCriteria criteria, int pageSize, int pageNumber){
        if (!(criteriaValidator.validateCriteria(criteria) && validator.isPageParamsValid(pageSize, pageNumber))){
            throw new IncorrectDataServiceException(INCORRECT_PARAMS_MSG);
        }
        if (!validator.isLongIdValid(userId)){
            throw new IncorrectDataServiceException(USERS_ID_NOT_VALID);
        }
        if (!userRepository.existsById(userId)){
            throw new NotFoundServiceException(String.format(USER_NOT_EXIST_MSG, userId));
        }

        Sort sort = Sort.by(criteria.getSortingField().attribute);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, criteria.getSortingOrder() == SortingOrder.DESC ?
                sort.descending() : sort.ascending());
        Page<Order> orderPage = orderRepository.getOrdersByUser_IdAndCostBetweenAndTimeOfPurchaseBetween(
                userId, criteria.getMinCost(), criteria.getMaxCost(), criteria.getMinTime(), criteria.getMaxTime(), pageable);

        return new PagedDTO<>(orderPage.get().collect(Collectors.toList()), new PagedModel.PageMetadata(
                pageSize, pageNumber, orderPage.getTotalElements(), orderPage.getTotalPages()));
    }

    @Override
    @Transactional
    public Order getOrder(Long userId, Long orderId){
        if (!(validator.isLongIdValid(orderId) && validator.isLongIdValid(userId))){
            throw new IncorrectDataServiceException(INCORRECT_ORDER_MSG);
        }
        return orderRepository.findOrderByUser_IdAndId(userId, orderId).orElseThrow(() ->
                new NotFoundServiceException(ORDER_NOT_EXIST_MSG));
    }
}
