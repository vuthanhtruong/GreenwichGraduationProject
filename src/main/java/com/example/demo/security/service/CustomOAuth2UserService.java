package com.example.demo.security.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    @Override
    @Transactional(readOnly = true)
    public OidcUser loadUser(OidcUserRequest userRequest) {
        return super.loadUser(userRequest);
    }
}
