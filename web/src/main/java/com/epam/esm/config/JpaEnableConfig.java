package com.epam.esm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@Configuration
@EnableJpaRepositories(basePackages = {"com.epam.esm.repository"})
public class JpaEnableConfig {
}
