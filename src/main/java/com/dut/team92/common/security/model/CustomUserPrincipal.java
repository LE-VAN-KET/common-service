package com.dut.team92.common.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class CustomUserPrincipal extends User {
    private String subId;
    private String auth;
    private String tokenType;
    private final String fullName;

    public CustomUserPrincipal(String username, String subId, String fullName, String tokenType, String auth,
                               Collection<? extends GrantedAuthority> authorities) {
        super(username, "", true, true, true, true, authorities);
        this.fullName = fullName;
        this.tokenType = tokenType;
        this.auth = auth;
        this.subId = subId;
    }

    public CustomUserPrincipal(String username, String password, String subId, String fullName, boolean enabled,
                               Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.fullName = fullName;
        this.subId = subId;
    }

    public CustomUserPrincipal(String username, String subId, String fullName, boolean enabled,
                               Collection<? extends GrantedAuthority> authorities) {
        super(username, "", enabled, true, true, true, authorities);
        this.fullName = fullName;
        this.subId = subId;
    }

    public String getAuth() {
        return auth;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSubId() {
        return subId;
    }
}
