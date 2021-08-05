package com.epam.esm.entity;


import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {

    USER(Set.of(Permission.CERTIFICATES_BUY, Permission.USERS_READ_SELF)),
    ADMIN(Set.of(Permission.values()));

    public final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name))
                .collect(Collectors.toSet());
    }
}
