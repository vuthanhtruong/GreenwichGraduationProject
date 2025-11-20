package com.example.demo.specialization.security.model;

import com.example.demo.user.person.model.Persons;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.Collection;
import java.util.Map;

public class CustomOidcUserPrincipal implements OidcUser {

    private final OidcUser delegate;
    private final Persons person;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOidcUserPrincipal(OidcUser delegate,
                                   Persons person,
                                   Collection<? extends GrantedAuthority> authorities) {
        this.delegate = delegate;
        this.person = person;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return delegate.getEmail(); // hoặc delegate.getName()
    }

    public Persons getPerson() {
        return person;
    }
}
