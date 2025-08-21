package com.example.demo.authenticator.dao;

import com.example.demo.authenticator.model.Authenticators;

public interface AuthenticatorsDAO {
    void createAuthenticator(Authenticators authenticator);
    void deleteAuthenticatorByPersonId(String personId);
}
