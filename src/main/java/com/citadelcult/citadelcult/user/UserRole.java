package com.citadelcult.citadelcult.user;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_CUSTOMER, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
