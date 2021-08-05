package com.epam.esm.validator.impl;

import com.epam.esm.criteria.CertificateCriteria;
import com.epam.esm.validator.CriteriaValidator;
import org.springframework.stereotype.Component;

@Component
public class CertificateCriteriaValidator implements CriteriaValidator<CertificateCriteria> {

    @Override
    public boolean validateCriteria(CertificateCriteria criteria) {

        return criteria != null && criteria.getMaxPrice() >= criteria.getMinPrice()
                && (criteria.getMaxCreateDate().isAfter(criteria.getMinCreateDate())
                || criteria.getMaxCreateDate().isEqual(criteria.getMinCreateDate()));
    }
}
