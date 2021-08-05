package com.epam.esm.security.provider;

import com.epam.esm.security.AuthDTO;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationAndTokenProvider {
    String authenticate(AuthDTO authDTO);
    String getUserName();
    Boolean hasAuthentication();
    Boolean containsAuthority(String authority);
}
