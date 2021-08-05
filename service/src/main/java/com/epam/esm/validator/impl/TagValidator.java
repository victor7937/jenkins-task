package com.epam.esm.validator.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.validator.ServiceValidator;
import org.springframework.stereotype.Component;

@Component
public class TagValidator implements ServiceValidator<Tag> {

    @Override
    public boolean validate(Tag model) {
        return model != null && model.getName() != null && !model.getName().isBlank();
    }

}
