package com.epam.esm.entity;

import com.epam.esm.audit.OrderAuditListener;
import com.epam.esm.util.CustomLocalDateTimeDeserializer;
import com.epam.esm.util.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "orders")
@EntityListeners(OrderAuditListener.class)
@NoArgsConstructor @Getter @Setter @ToString
public class Order implements Serializable {

    private static final long serialVersionUID = -2459886736436355958L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "time_of_purchase")
    private LocalDateTime timeOfPurchase;

    @Column(name = "cost")
    private Float cost;

    @ManyToOne(cascade = {CascadeType.DETACH,
            CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "certificate_id")
    private GiftCertificate giftCertificate;

    @ManyToOne(cascade = {CascadeType.DETACH,
            CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "users_id")
    private User user;


    public Order(Long id, LocalDateTime timeOfPurchase, Float cost) {
        this.id = id;
        this.timeOfPurchase = timeOfPurchase;
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(timeOfPurchase, order.timeOfPurchase) && Objects.equals(cost, order.cost) && Objects.equals(giftCertificate, order.giftCertificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeOfPurchase, cost, giftCertificate);
    }

}
