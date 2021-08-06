package com.epam.esm.controller;

import com.epam.esm.criteria.OrderCriteria;
import com.epam.esm.criteria.UserCriteria;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.hateoas.assembler.OrderAssembler;
import com.epam.esm.hateoas.assembler.UserAssembler;
import com.epam.esm.hateoas.model.OrderModel;
import com.epam.esm.hateoas.model.UserModel;
import com.epam.esm.security.provider.AuthenticationAndTokenProvider;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Map;


/**
 * Controller for REST operations with users and its orders
 * Makes get and get by id operations of users and their orders
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final UserAssembler userAssembler;
    private final OrderAssembler orderAssembler;
    private final AuthenticationAndTokenProvider authAndTokenProvider;


    @Autowired
    public UserController(UserService userService, OrderService orderService, UserAssembler userAssembler,
                          OrderAssembler orderAssembler, AuthenticationAndTokenProvider authAndTokenProvider) {
        this.userService = userService;
        this.orderService = orderService;
        this.userAssembler = userAssembler;
        this.orderAssembler = orderAssembler;
        this.authAndTokenProvider = authAndTokenProvider;
    }

    /**
     * Get method for receiving a page of users by some criteria
     * @param size - size of page
     * @param page - number of current page
     * @param criteriaParams other params for filtering and sorting
     * @return Page of users
     */
    @GetMapping(produces = { "application/prs.hal-forms+json" })
    @PreAuthorize("hasAuthority('users:read')")
    public PagedModel<UserModel> getUsers(@RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                          @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                          @RequestParam Map<String, String> criteriaParams){

        UserCriteria criteria = UserCriteria.createCriteria(criteriaParams);
        PagedDTO<User> pagedDTO = userService.get(criteria, size, page);
        if (pagedDTO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return userAssembler.toPagedModel(pagedDTO.getPage(), pagedDTO.getPageMetadata(), criteria);
    }

    /**
     * Get method for finding user by its email
     * @param id - id of a user
     * @return user found
     */
    @GetMapping(value = "/{id}", produces = { "application/prs.hal-forms+json" })
    @PreAuthorize("hasPermission(#id,'users:read')")
    public UserModel getById(@PathVariable("id") Long id){
        User user = userService.getById(id);
        return userAssembler.toModel(user);
    }

    /**
     * Get method for receiving a page of orders by some criteria
     * @param id - id of a user
     * @param size - size of a page
     * @param page - current page
     * @param criteriaParams - Other params for filtering and sorting
     * @return page of orders
     */
    @GetMapping(value = "/{id}/orders", produces = { "application/prs.hal-forms+json" })
    @PreAuthorize("hasPermission(#id,'orders:read')")
    public PagedModel<OrderModel> getOrdersOfUser(@PathVariable("id") Long id,
                                                  @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                  @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                  @RequestParam Map<String, String> criteriaParams){

        OrderCriteria criteria = OrderCriteria.createCriteria(criteriaParams);
        PagedDTO<Order> pagedDTO = orderService.getOrdersOfUser(id, criteria, size, page);
        if (pagedDTO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return orderAssembler.toPagedModel(pagedDTO.getPage(), pagedDTO.getPageMetadata(), criteria);
    }

    /**
     * Get method for receiving an order of a user by its id
     * @param id - users id
     * @param orderId - orders id
     * @return order found
     */
    @GetMapping(value = "/{id}/orders/{orderId}", produces = { "application/prs.hal-forms+json" })
    @PreAuthorize("hasPermission(#id,'orders:read')")
    public OrderModel getOrderOfUser(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId){
        Order order = orderService.getOrder(id, orderId);
        return orderAssembler.toModel(order);
    }

    /**
     * Endpoint for authenticated user to get self info
     * @return users info
     */
    @GetMapping(value = "/me", produces = { "application/prs.hal-forms+json" })
    @PreAuthorize("hasAuthority('users:read-self')")
    public UserModel getSelfUser(Principal principal){
        String email = principal.getName();
        User user = userService.getByEmail(email);
        return userAssembler.toModel(user);
    }

    
}
