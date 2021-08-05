package com.epam.esm.audit;

import com.epam.esm.entity.Role;
import com.epam.esm.entity.User;

import javax.persistence.PrePersist;

public class UserAuditListener {

    @PrePersist
    public void setRoleAndActiveBeforeCreating(User user){
        user.setRole(Role.USER);
        user.setActive(true);
    }
}
