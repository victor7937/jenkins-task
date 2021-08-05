package com.epam.esm.validator.impl;

import com.epam.esm.dto.CertificateDTO;
import com.epam.esm.validator.ServiceValidator;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class CertificateValidator implements ServiceValidator<CertificateDTO> {

    @Override
    public boolean validate(CertificateDTO model) {
        return model != null && model.getName() != null && !model.getName().isBlank() && model.getPrice() != null
                && model.getPrice() >= 0.0f && model.getDuration() != null && model.getDuration() > 0
                && model.getTags() != null && model.getTags().stream().noneMatch(Objects::isNull)
                && model.getTags().stream().noneMatch(t -> t.getName() == null || t.getName().isBlank());
    }

}
