package com.epam.esm.dto;


import com.epam.esm.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDTO {

    private String name;

    private String description;

    private Float price;

    private Integer duration;

    private Set<Tag> tags = new LinkedHashSet<>();
}
