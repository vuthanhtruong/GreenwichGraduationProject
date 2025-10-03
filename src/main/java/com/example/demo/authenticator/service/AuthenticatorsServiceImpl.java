package com.example.demo.authenticator.service;

import com.example.demo.authenticator.dao.AuthenticatorsDAO;
import com.example.demo.authenticator.model.Authenticators;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthenticatorsServiceImpl implements AuthenticatorsService {
    @Override
    public Authenticators getAuthenticatorByPersonId(String personId) {
        return authenticatorsDAO.getAuthenticatorByPersonId(personId);
    }

    @Override
    public Map<String, String> validatePasswordChange(String personId, String currentPassword, String newPassword, String confirmNewPassword) {
        return authenticatorsDAO.validatePasswordChange(personId, currentPassword, newPassword, confirmNewPassword);
    }

    @Override
    public void changePassword(String personId, String newPassword) {
        authenticatorsDAO.changePassword(personId, newPassword);
    }

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
