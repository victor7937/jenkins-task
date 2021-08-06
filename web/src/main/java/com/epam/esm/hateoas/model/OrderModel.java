package com.epam.esm.hateoas.model;

import com.epam.esm.util.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Relation(itemRelation = "order", collectionRelation = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel extends RepresentationModel<OrderModel> {

    private Long id;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime timeOfPurchase;

    private Float cost;

    @JsonIgnore
    private GiftCertificateModel giftCertificate;

    @JsonIgnore
    private UserModel user;

    public OrderModel(Long id, LocalDateTime timeOfPurchase, Float cost) {
        this.id = id;
        this.timeOfPurchase = timeOfPurchase;
        this.cost = cost;
    }

}
