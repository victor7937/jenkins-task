package com.epam.esm.validator.impl;

import com.epam.esm.dto.OrderDTO;
import com.epam.esm.validator.ServiceValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator implements ServiceValidator<OrderDTO> {

    @Override
    public boolean validate(OrderDTO model) {
        return model != null && isLongIdValid(model.getId()) && isStringIdValid(model.getEmail());
    }

    @Override
    public boolean isStringIdValid(String id) {
        return ServiceValidator.super.isStringIdValid(id) && EmailValidator.getInstance().isValid(id);
    }
}
