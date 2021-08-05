package com.epam.esm.security.service;

import com.epam.esm.entity.User;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.security.UserAccountDetails;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserAccountDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user;
        try {
            user = userService.getByEmail(s);
        } catch (ServiceException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
        return UserAccountDetails.of(user);
    }
}
