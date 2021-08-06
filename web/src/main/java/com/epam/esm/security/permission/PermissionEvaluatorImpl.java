package com.epam.esm.security.permission;

import com.epam.esm.security.UserAccountDetails;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object id, Object permission) {
        boolean isPermissionFound = authentication.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(permission));
        if (isPermissionFound){
            return true;
        }
        UserAccountDetails userAccountDetails = (UserAccountDetails) authentication.getPrincipal();
        return Objects.equals(id, userAccountDetails.getId());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
