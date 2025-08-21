package com.example.demo.authenticator.service;

import com.example.demo.authenticator.dao.AuthenticatorsDAO;
import com.example.demo.authenticator.model.Authenticators;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatorsServiceImpl implements AuthenticatorsService {
    @Override
    public void deleteAuthenticatorByPersonId(String personId) {
        authenticatorsDAO.deleteAuthenticatorByPersonId(personId);
    }

    private final AuthenticatorsDAO authenticatorsDAO;

    public AuthenticatorsServiceImpl(AuthenticatorsDAO authenticatorsDAO) {
        this.authenticatorsDAO = authenticatorsDAO;
    }

    @Override
    public void createAuthenticator(Authenticators authenticator) {
        authenticatorsDAO.createAuthenticator(authenticator);
    }
}
