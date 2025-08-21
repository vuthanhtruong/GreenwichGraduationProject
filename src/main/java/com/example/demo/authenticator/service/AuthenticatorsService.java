package com.example.demo.authenticator.service;

import com.example.demo.authenticator.model.Authenticators;

public interface AuthenticatorsService {
    void createAuthenticator(Authenticators authenticator);
    void deleteAuthenticatorByPersonId(String personId);
}
