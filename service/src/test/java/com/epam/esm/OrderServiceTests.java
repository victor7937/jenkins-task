package com.epam.esm;

import com.epam.esm.criteria.OrderCriteria;
import com.epam.esm.dto.OrderDTO;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.exception.IncorrectDataServiceException;
import com.epam.esm.exception.NotFoundServiceException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.impl.OrderServiceImpl;
import com.epam.esm.validator.impl.OrderCriteriaValidator;
import com.epam.esm.validator.impl.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {


    @Mock
    OrderRepository orderRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GiftCertificateRepository certificateRepository;

    OrderService service;

    final Order orderSample = new Order(1L, LocalDateTime.now(), 1f);
    final List<Order> orderListSample = List.of(new Order(1L, LocalDateTime.now(), 1f),
            new Order(2L, LocalDateTime.now(), 2f),
            new Order(3L, LocalDateTime.now(), 3f));
    final PagedDTO<Order> orderPagedDtoSample = new PagedDTO<>(orderListSample, new PagedModel.PageMetadata(1,1,1));
    final OrderCriteria criteriaSample = OrderCriteria.createCriteria(new HashMap<>());
    final OrderDTO sampleDTO = new OrderDTO("someemail@mail.ru", 1L);
    //final String userIdSample = "user@mail.com";
    final long idSample = 1L;
    final Long userIdSample = 1L;

    @BeforeEach
    void init(){
        service = new OrderServiceImpl(orderRepository, userRepository, certificateRepository, new OrderCriteriaValidator(), new OrderValidator());
    }

    @Nested
    class GetOrdersTests{

        @Test
        void gettingWithIncorrectPageParamsShouldRaiseException(){
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class, () -> service.getOrdersOfUser(userIdSample, criteriaSample,-1,2)),
                    () -> assertThrows(IncorrectDataServiceException.class, () -> service.getOrdersOfUser(userIdSample, criteriaSample,2,-1)),
                    () -> assertThrows(IncorrectDataServiceException.class, () -> service.getOrdersOfUser(userIdSample, criteriaSample,0,0))
            );
            verify(orderRepository, never()).getOrdersByUser_IdAndCostBetweenAndTimeOfPurchaseBetween(any(), any(), any(), any(),any(), any());
        }

        @Test
        void requestForNonExistentPageShouldRaiseException(){
            when(orderRepository.getOrdersByUser_IdAndCostBetweenAndTimeOfPurchaseBetween(anyLong(),anyFloat(), anyFloat(),
                    any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class) )).thenReturn(Page.empty());
            when(userRepository.existsById(userIdSample)).thenReturn(true);
            assertTrue(service.getOrdersOfUser(userIdSample, criteriaSample, 1, 999999).isEmpty());
            verify(orderRepository).getOrdersByUser_IdAndCostBetweenAndTimeOfPurchaseBetween(anyLong(),anyFloat(), anyFloat(),
                    any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        }

        @Test
        void gettingWithIncorrectEmailShouldRaiseException(){
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrdersOfUser(null, criteriaSample, 1,1)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrdersOfUser(-1L, criteriaSample, 1,1)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrdersOfUser(0L, criteriaSample, 1,1))
            );
            verify(orderRepository, never()).getOrdersByUser_IdAndCostBetweenAndTimeOfPurchaseBetween(anyLong(),anyFloat(), anyFloat(),
                    any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        }
    }

    @Nested
    class GettingByIdTests {

        @Test
        void correctGettingByIdShouldReturnOrder(){
            Order expected = orderSample;
            when(orderRepository.findOrderByUser_IdAndId(userIdSample, idSample)).thenReturn(Optional.of(expected));
            assertEquals(expected, service.getOrder(userIdSample, idSample));
            verify(orderRepository).findOrderByUser_IdAndId(anyLong(), anyLong());
        }

        @Test
        void gettingWithNotExistentIdOrIdShouldRaiseException(){
            Long notExistentUserId = 393939L;
            Long notExistentId = 99999L;
            when(orderRepository.findOrderByUser_IdAndId(notExistentUserId, notExistentId)).thenReturn(Optional.empty());
            assertThrows(NotFoundServiceException.class,() -> service.getOrder(notExistentUserId, notExistentId));

            verify(orderRepository).findOrderByUser_IdAndId(anyLong(), anyLong());
        }

        @Test
        void gettingWithIncorrectUserIdOrOrderIdShouldRaiseException(){
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrder(null, idSample)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrder(userIdSample, null)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrder(null, null)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrder(-1L, idSample)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrder(0L, idSample)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrder(userIdSample, 0L)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getOrder(userIdSample, -1L))
            );
            verify(orderRepository, never()).findOrderByUser_IdAndId(anyLong(), anyLong());
        }
    }

    @Nested
    class MakingOrderTests {

        Order completeOrder;
        String userEmailSample = "good_email@email.com";
        User userFound = new User(sampleDTO.getEmail(), "someName", "someSurname");
        GiftCertificate giftCertificateFound = new GiftCertificate(sampleDTO.getId(), "someString","someString",
                1.1f, 5, LocalDateTime.now(), LocalDateTime.now());

        @BeforeEach
        void initMakingOrder(){
            completeOrder = new Order();
            completeOrder.setUser(userFound);
            completeOrder.setGiftCertificate(giftCertificateFound);
            completeOrder.setCost(giftCertificateFound.getPrice());
        }

        @Test
        void correctOrderingShouldReturnCompleteOrder () {
            when(orderRepository.save(any(Order.class))).thenReturn(completeOrder);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userFound));
            when(certificateRepository.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(giftCertificateFound));
            assertEquals(completeOrder, service.makeOrder(sampleDTO));
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        void orderingWithNotExistentUserIdOrIdShouldRaiseException(){
            String notExistentEmail = "some_email@some.com";
            Long notExistentId = 99999L;

            lenient().when(userRepository.findByEmail(notExistentEmail)).thenReturn(Optional.empty());
            lenient().when(userRepository.findByEmail(userEmailSample)).thenReturn(Optional.ofNullable(userFound));
            lenient().when(certificateRepository.findById(notExistentId)).thenReturn(Optional.empty());
            lenient().when(certificateRepository.findById(idSample)).thenReturn(Optional.ofNullable(giftCertificateFound));

            OrderDTO incorrectDto1 = new OrderDTO(notExistentEmail, idSample);
            OrderDTO incorrectDto2 = new OrderDTO(userEmailSample, notExistentId);
            OrderDTO incorrectDto3 = new OrderDTO(notExistentEmail, notExistentId);
            assertAll(
                    () ->  assertThrows(NotFoundServiceException.class,() -> service.makeOrder(incorrectDto1)),
                    () ->  assertThrows(NotFoundServiceException.class,() -> service.makeOrder(incorrectDto2)),
                    () ->  assertThrows(NotFoundServiceException.class,() -> service.makeOrder(incorrectDto3))
            );

            verify(orderRepository, never()).save(any(Order.class));
        }


        @Test
        void addingIncorrectOrderShouldRaiseException() {
            OrderDTO incorrectOrderDto1 = new OrderDTO(null, idSample);
            OrderDTO incorrectOrderDto2 = new OrderDTO(userEmailSample, null);
            OrderDTO incorrectOrderDto3 = new OrderDTO("", idSample);
            OrderDTO incorrectOrderDto4 = new OrderDTO("somenotemail", idSample);
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.makeOrder(null)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.makeOrder(incorrectOrderDto1)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.makeOrder(incorrectOrderDto2)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.makeOrder(incorrectOrderDto3)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.makeOrder(incorrectOrderDto4))
            );

            verify(orderRepository, never()).save(any(Order.class));
            verify(userRepository, never()).findByEmail(anyString());
            verify(certificateRepository, never()).findById(anyLong());
        }
    }

}
