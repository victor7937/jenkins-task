package com.epam.esm;

import com.epam.esm.controller.UserController;
import com.epam.esm.criteria.OrderCriteria;
import com.epam.esm.criteria.UserCriteria;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.dto.UserDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.exception.IncorrectDataServiceException;
import com.epam.esm.exception.NotFoundServiceException;
import com.epam.esm.hateoas.assembler.OrderAssembler;
import com.epam.esm.hateoas.assembler.UserAssembler;
import com.epam.esm.security.SecurityConfig;
import com.epam.esm.security.jwt.JwtTokenFilter;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.hateoas.PagedModel;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtTokenFilter.class}))
class UserControllerTests {

    @TestConfiguration
    static class UserControllerConfiguration {

        @Bean
        public UserAssembler UserAssembler() {
            return new UserAssembler(new ModelMapper());
        }

        @Bean
        public OrderAssembler orderAssembler() {
            return new OrderAssembler(new ModelMapper());
        }
    }

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private OrderService orderService;

    final User userSample1 = new User("someuser@gmail.com","Sergey", "Smith");
    final List<User> userListSample = List.of(new User("someuser@gmail.com","Sergey", "Smith"),
            new User("someuser2@gmail.com","Sergey2", "Smith2"),
            new User("someuser3@gmail.com","Sergey3", "Smith3"));

    final Order orderSample = new Order(1L, LocalDateTime.now(), 1f);
    final List<Order> orderListSample = List.of(new Order(1L, LocalDateTime.now(), 1f),
            new Order(2L, LocalDateTime.now(), 2f),
            new Order(3L, LocalDateTime.now(), 3f));


    @Test
    void correctGettingByIdShouldBeOk() throws Exception {
        User expected = userSample1;
        expected.setId(1L);
        when(userService.getById(expected.getId())).thenReturn(expected);

        mvc.perform(get("/users/{id}",expected.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.firstName").value(expected.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expected.getLastName()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()));
    }

    @Test
    void gettingWithIncorrectIdShouldBeNotFound() throws Exception {
        when(userService.getById(anyLong())).thenThrow(NotFoundServiceException.class);

        mvc.perform(get("/users/{id}",99))
                .andExpect(status().isNotFound());
    }

    @Test
    void gettingWithIncorrectParamsShouldBeBadRequest() throws Exception {
        when(userService.getById(anyLong())).thenThrow(IncorrectDataServiceException.class);

        mvc.perform(get("/users/{id}",-1))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/users/dfdf"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void correctGettingAllShouldBeOk() throws Exception {
        int pageSize = 5;
        int pageNumber = 1;
        PagedDTO<User> expectedDto = new PagedDTO<>(userListSample, new PagedModel.PageMetadata(pageSize, pageNumber, userListSample.size()));
        when(userService.get(any(UserCriteria.class), anyInt(), anyInt())).thenReturn(expectedDto);

        mvc.perform(get("/users").param("size","" + pageSize).param("page", "" + pageNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.users.length()").value(userListSample.size()));
    }

    @Test
    void GettingEmptyResultShouldBeNoContent() throws Exception {
        PagedDTO<User> emptyDto = new PagedDTO<>();
        when(userService.get(any(UserCriteria.class), anyInt(), anyInt())).thenReturn(emptyDto);

        mvc.perform(get("/users")).andExpect(status().isNoContent());
    }

    @Test
    void correctGettingOrderByUserIdAndOrderIdShouldBeOk() throws Exception {
        Order expected = orderSample;
        userSample1.setId(1L);
        expected.setUser(userSample1);
        expected.setGiftCertificate(new GiftCertificate());
        when(orderService.getOrder(userSample1.getId(), expected.getId())).thenReturn(expected);

        mvc.perform(get("/users/{id}/orders/{orderId}",userSample1.getId(), expected.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.cost").value(expected.getCost()))
                .andExpect(jsonPath("$.timeOfPurchase").exists());
    }

    @Test
    void gettingOrderWithIncorrectIdsShouldBeNotFound() throws Exception {
        when(orderService.getOrder(anyLong(), anyLong())).thenThrow(NotFoundServiceException.class);

        mvc.perform(get("/users/{id}/orders/{orderId}",1, 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void gettingOrderWithIncorrectParamsShouldBeBadRequest() throws Exception {
        when(orderService.getOrder(anyLong(), anyLong())).thenThrow(IncorrectDataServiceException.class);

        mvc.perform(get("/users/{id}/orders/{orderId}",0, -5))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/users/oods/orders/{orderId}",1))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/users/{id}/orders/a",1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void correctGettingOrdersOfAUserShouldBeOk() throws Exception {
        int pageSize = 5;
        int pageNumber = 1;
        userSample1.setId(1L);
        orderListSample.forEach(o -> {
            o.setUser(userSample1);
            o.setGiftCertificate(new GiftCertificate());
        });

        PagedDTO<Order> expectedDto = new PagedDTO<>(orderListSample, new PagedModel.PageMetadata(pageSize, pageNumber, orderListSample.size()));
        when(orderService.getOrdersOfUser(anyLong(), any(OrderCriteria.class), anyInt(), anyInt())).thenReturn(expectedDto);

        mvc.perform(get("/users/{id}/orders", 1).param("size","" + pageSize).param("page", "" + pageNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.orders.length()").value(orderListSample.size()));
    }

    @Test
    void resultWithNoOrdersShouldBeNoContent() throws Exception {
        PagedDTO<Order> emptyDto = new PagedDTO<>();
        when(orderService.getOrdersOfUser(anyLong(), any(OrderCriteria.class), anyInt(), anyInt())).thenReturn(emptyDto);

        mvc.perform(get("/users/{id}/orders", 1)).andExpect(status().isNoContent());
    }




}
