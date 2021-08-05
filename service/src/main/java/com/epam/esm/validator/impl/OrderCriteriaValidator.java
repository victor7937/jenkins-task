package com.epam.esm.validator.impl;

import com.epam.esm.criteria.OrderCriteria;
import com.epam.esm.validator.CriteriaValidator;
import org.springframework.stereotype.Component;

@Component
public class OrderCriteriaValidator implements CriteriaValidator<OrderCriteria> {

    @Override
    public boolean validateCriteria(OrderCriteria criteria) {
        return criteria != null && criteria.getMaxCost() >= criteria.getMinCost()
                && (criteria.getMaxTime().isAfter(criteria.getMinTime())
                || criteria.getMaxTime().isEqual(criteria.getMinTime()));
    }
}
