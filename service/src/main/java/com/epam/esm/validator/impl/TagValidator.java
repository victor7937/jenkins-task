package com.epam.esm.validator.impl;

import com.epam.esm.dto.TagDTO;
import com.epam.esm.validator.ServiceValidator;
import org.springframework.stereotype.Component;

@Component
public class TagValidator implements ServiceValidator<TagDTO> {

    @Override
    public boolean validate(TagDTO model) {
        return model != null && model.getName() != null && !model.getName().isBlank();
    }

}
