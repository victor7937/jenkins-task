package com.epam.esm.validator.impl;

import com.epam.esm.dto.UserDTO;
import com.epam.esm.entity.User;
import com.epam.esm.validator.ServiceValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class UserValidator implements ServiceValidator<UserDTO> {

    @Override
    public boolean validate(UserDTO model) {
        return model != null && model.getEmail() != null && isStringIdValid(model.getEmail())
                && model.getPassword() != null && !model.getPassword().isBlank();
    }

    @Override
    public boolean isStringIdValid(String id) {
        return ServiceValidator.super.isStringIdValid(id) && EmailValidator.getInstance().isValid(id);
    }
}
