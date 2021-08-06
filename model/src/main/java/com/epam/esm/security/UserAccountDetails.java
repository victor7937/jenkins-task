package com.epam.esm.security;

import com.epam.esm.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDetails implements UserDetails {

    private String email;

    private String password;

    private Long id;

    private Collection<? extends GrantedAuthority> authorities;

    private Boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Long getId() {
        return id;
    }

    public static UserAccountDetails of(User user){
        return new UserAccountDetails(user.getEmail(), user.getPassword(), user.getId(), user.getRole().getAuthorities(), user.isActive());
    }
}
