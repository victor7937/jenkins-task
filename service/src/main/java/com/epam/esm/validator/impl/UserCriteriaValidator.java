package com.epam.esm.validator.impl;

import com.epam.esm.criteria.UserCriteria;
import com.epam.esm.validator.CriteriaValidator;
import org.springframework.stereotype.Component;

@Component
public class UserCriteriaValidator implements CriteriaValidator<UserCriteria> {

    @Override
    public boolean validateCriteria(UserCriteria criteria) {
        return criteria != null;
    }
}
