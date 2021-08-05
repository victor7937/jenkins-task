package com.epam.esm.audit;

import com.epam.esm.entity.Order;

import javax.persistence.PrePersist;
import java.time.LocalDateTime;

public class OrderAuditListener {

    @PrePersist
    public void setDateTimeBeforeCreating(Order order){
        order.setTimeOfPurchase(LocalDateTime.now());
    }
}
