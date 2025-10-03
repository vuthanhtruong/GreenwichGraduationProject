package com.example.demo.authenticator.dao;

import com.example.demo.authenticator.model.Authenticators;

import java.util.Map;

public interface AuthenticatorsDAO {
    void createAuthenticator(Authenticators authenticator);
    void deleteAuthenticatorByPersonId(String personId);
    Authenticators getAuthenticatorByPersonId(String personId);
    Map<String, String> validatePasswordChange(String personId, String currentPassword, String newPassword, String confirmNewPassword);
    void changePassword(String personId, String newPassword);
}
