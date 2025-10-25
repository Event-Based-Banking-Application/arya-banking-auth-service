package org.arya.banking.auth.service;

import org.arya.banking.common.dto.KeyCloakResponse;
import org.arya.banking.common.model.KeyCloakUser;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeyCloakService {

    KeyCloakResponse createKeyCloakUser(KeyCloakUser keyCloakUser);

    String authenticateUser(String username, String password);

    UserRepresentation findUserByUsername(String username);

    List<UserRepresentation> findUserByUserId(String userId);
}
