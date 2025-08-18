package com.example.demo.service;

import com.example.demo.entity.Authenticators;

public interface AuthenticatorsService {
    void createAuthenticator(Authenticators authenticator);
    void deleteAuthenticatorByPersonId(String personId);
}
