package com.example.demo.service.impl;

import com.example.demo.dao.AuthenticatorsDAO;
import com.example.demo.entity.Authenticators;
import com.example.demo.service.AuthenticatorsService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatorsServiceImpl implements AuthenticatorsService {
    private final AuthenticatorsDAO authenticatorsDAO;

    public AuthenticatorsServiceImpl(AuthenticatorsDAO authenticatorsDAO) {
        this.authenticatorsDAO = authenticatorsDAO;
    }

    @Override
    public void createAuthenticator(Authenticators authenticator) {
        authenticatorsDAO.createAuthenticator(authenticator);
    }
}
