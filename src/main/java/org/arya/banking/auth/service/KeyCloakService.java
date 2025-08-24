package org.arya.banking.auth.service;

import jakarta.ws.rs.core.Response;
import org.arya.banking.common.model.KeyCloakUser;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeyCloakService {

    Response createKeyCloakUser(KeyCloakUser keyCloakUser);

    List<UserRepresentation> findUserByEmailId(String emailId);

    List<UserRepresentation> findUserByUserId(String userId);
}
