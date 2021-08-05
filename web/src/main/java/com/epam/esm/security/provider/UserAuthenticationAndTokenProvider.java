package com.epam.esm.security.provider;

import com.epam.esm.entity.Permission;
import com.epam.esm.security.AuthDTO;
import com.epam.esm.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;

@Component
public class UserAuthenticationAndTokenProvider implements AuthenticationAndTokenProvider {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    @Autowired
    public UserAuthenticationAndTokenProvider(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String authenticate(AuthDTO authDTO){
        String email = authDTO.getEmail();
        String password = authDTO.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return tokenProvider.createToken(email);
    }

    @Override
    public String getUserName() {
         return getAuthentication().getName();
    }

    @Override
    public Boolean hasAuthentication() {
        Authentication authentication = getAuthentication();
        return !(authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated());

    }

    @Override
    public Boolean containsAuthority(String authority) {
        Authentication authentication = getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(authority));
    }

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
